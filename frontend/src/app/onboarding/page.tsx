'use client';

import React, { useState } from 'react';
import { Container, Paper, Typography, TextField, Stack, Button } from '@mui/material';
import { checkNickname, checkLolTag, completeOnboarding, syncMyUser } from '@/services/auth';
import { useRouter } from 'next/navigation';

export default function OnboardingPage() {
    const router = useRouter();
    const [nickname, setNickname] = useState('');
    const [nicknameOk, setNicknameOk] = useState<boolean | null>(null);
    const [lolTag, setLolTag] = useState('');
    const [lolTagOk, setLolTagOk] = useState<boolean | null>(null);
    const [submitting, setSubmitting] = useState(false);

    const onCheckNickname = async () => {
        const res = await checkNickname(nickname);
        setNicknameOk(Boolean(res));
    };

    const onCheckLolTag = async () => {
        const res = await checkLolTag(lolTag);
        setLolTagOk(Boolean(res));
    };

    const onSubmit = async () => {
        setSubmitting(true);
        try {
            await completeOnboarding({ nickname, lolTag });
            await syncMyUser();
            router.replace('/');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Container maxWidth="sm" sx={{ py: 8 }}>
            <Typography variant="h5" gutterBottom>최초 설정</Typography>
            <Paper sx={{ p: 4 }}>
                <Stack spacing={2}>
                    <TextField label="라이엇 닉네임#태그" value={lolTag} onChange={(e) => setLolTag(e.target.value)} fullWidth />
                    <Button variant="outlined" onClick={onCheckLolTag} disabled={!lolTag}>롤 태그 중복 확인</Button>
                    {lolTagOk === true && <Typography color="success.main">사용 가능합니다.</Typography>}
                    {lolTagOk === false && <Typography color="error.main">이미 사용 중입니다.</Typography>}

                    <TextField label="닉네임" value={nickname} onChange={(e) => setNickname(e.target.value)} fullWidth />
                    <Button variant="outlined" onClick={onCheckNickname} disabled={!nickname}>닉네임 중복 확인</Button>
                    {nicknameOk === true && <Typography color="success.main">사용 가능합니다.</Typography>}
                    {nicknameOk === false && <Typography color="error.main">이미 사용 중입니다.</Typography>}

                    <Button variant="contained" onClick={onSubmit} disabled={!nicknameOk || !lolTagOk || submitting}>
                        {submitting ? '저장 중...' : '완료'}
                    </Button>
                </Stack>
            </Paper>
        </Container>
    );
}



