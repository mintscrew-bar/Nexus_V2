'use client';

import React, { useState, useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Container, Paper, TextField, Typography, Button, Stack, Divider, Alert } from '@mui/material';
import { useAuthStore } from '@/stores/authStore';
import OAuthButtons from '@/components/auth/OAuthButtons';

function LoginPageInner() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const { login } = useAuthStore();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const oauthError = searchParams.get('error');
        if (oauthError) {
            const errorMessages: Record<string, string> = {
                'authentication_failed': 'OAuth 인증에 실패했습니다. 다시 시도해주세요.',
                'processing_failed': '로그인 처리 중 오류가 발생했습니다.',
                'oauth2_failed': 'OAuth2 인증에 실패했습니다.',
                'no_token': '토큰을 받지 못했습니다. 다시 시도해주세요.',
            };
            setError(errorMessages[oauthError] || '알 수 없는 오류가 발생했습니다.');
        }
    }, [searchParams]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await login(username, password);
            router.push('/');
        } catch (err: any) {
            setError(err.message || '로그인에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container maxWidth="sm" sx={{ py: 10 }}>
            <Paper sx={{ p: 4 }}>
                <Typography variant="h5" gutterBottom>로그인</Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
                        {error}
                    </Alert>
                )}

                <form onSubmit={handleSubmit}>
                    <Stack spacing={2}>
                        <TextField
                            label="이메일"
                            type="email"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            autoFocus
                            fullWidth
                            error={!!error}
                        />
                        <TextField
                            label="비밀번호"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            fullWidth
                            error={!!error}
                        />
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={loading || !username || !password}
                            size="large"
                        >
                            {loading ? '로그인 중...' : '로그인'}
                        </Button>

                        <Divider>또는</Divider>
                        <OAuthButtons />

                        <Button
                            href="/register"
                            variant="outlined"
                            size="large"
                        >
                            회원가입
                        </Button>
                    </Stack>
                </form>
            </Paper>
        </Container>
    );
}

export default function LoginPage() {
    return (
        <Suspense fallback={
            <Container maxWidth="sm" sx={{ py: 10 }}>
                <Paper sx={{ p: 4 }}>
                    <Typography variant="h5" gutterBottom>로그인</Typography>
                    <Stack spacing={2}>
                        <TextField label="이메일" disabled fullWidth />
                        <TextField label="비밀번호" type="password" disabled fullWidth />
                        <Button variant="contained" disabled size="large">
                            로딩중...
                        </Button>
                    </Stack>
                </Paper>
            </Container>
        }>
            <LoginPageInner />
        </Suspense>
    );
}


