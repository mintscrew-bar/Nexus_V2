'use client';

import React, { useEffect, Suspense } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { Container, CircularProgress, Box, Typography } from '@mui/material';

function OAuthCallbackInner() {
    const params = useSearchParams();
    const router = useRouter();
    const { setAuth } = useAuthStore();

    useEffect(() => {
        const token = params.get('token');
        const error = params.get('error');
        const username = params.get('username') || undefined;

        if (error) {
            console.error('OAuth callback error:', error);
            // 에러가 있으면 로그인 페이지로 이동하며 에러 메시지 전달
            router.replace(`/login?error=${encodeURIComponent(error)}`);
            return;
        }

        if (token) {
            setAuth(token, username);
            // OAuth2로 로그인한 사용자는 온보딩 페이지로 이동
            router.replace('/onboarding');
        } else {
            // 토큰도 에러도 없으면 로그인 페이지로
            router.replace('/login?error=no_token');
        }
    }, [params, router, setAuth]);

    return (
        <Container sx={{ py: 10 }}>
            <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="40vh">
                <CircularProgress size={60} />
                <Typography variant="h6" sx={{ mt: 2 }}>로그인 처리 중...</Typography>
                <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
                    잠시만 기다려주세요.
                </Typography>
            </Box>
        </Container>
    );
}

export default function OAuthCallbackPage() {
    return (
        <Suspense fallback={
            <Container sx={{ py: 10 }}>
                <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center">
                    <CircularProgress />
                    <Typography variant="body2" sx={{ mt: 2 }}>로그인 처리 중...</Typography>
                </Box>
            </Container>
        }>
            <OAuthCallbackInner />
        </Suspense>
    );
}



