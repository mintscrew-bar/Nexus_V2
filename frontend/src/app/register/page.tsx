'use client';

import React from 'react';
import { Container, Paper, Typography } from '@mui/material';
import RegisterWizard from '@/components/auth/RegisterWizard';

export default function RegisterPage() {
    return (
        <Container maxWidth="md" sx={{ py: 6 }}>
            <Typography variant="h4" gutterBottom>회원가입</Typography>
            <Paper sx={{ p: 4 }}>
                <RegisterWizard />
            </Paper>
        </Container>
    );
}



