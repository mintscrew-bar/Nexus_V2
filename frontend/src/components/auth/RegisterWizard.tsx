'use client';

import React, { useState } from 'react';
import { Stepper, Step, StepLabel, Button, Box, TextField, Checkbox, FormControlLabel, Stack, Typography } from '@mui/material';
import { checkNickname, checkLolTag, registerUser, requestEmailCode, verifyEmailCode } from '@/services/auth';

type StepKey = 'agreements' | 'email' | 'emailVerify' | 'nickname' | 'password' | 'loltag';

const steps: { key: StepKey; label: string }[] = [
    { key: 'agreements', label: '약관 동의' },
    { key: 'email', label: '이메일 입력' },
    { key: 'emailVerify', label: '이메일 인증' },
    { key: 'nickname', label: '닉네임' },
    { key: 'password', label: '비밀번호' },
    { key: 'loltag', label: '롤 태그' },
];

const RegisterWizard: React.FC = () => {
    const [activeStep, setActiveStep] = useState(0);
    const [agreements, setAgreements] = useState({ terms: false, privacy: false, marketing: false });
    const [email, setEmail] = useState('');
    const [emailCode, setEmailCode] = useState('');
    const [emailRequested, setEmailRequested] = useState(false);
    const [emailVerified, setEmailVerified] = useState(false);
    const [nickname, setNickname] = useState('');
    const [nicknameOk, setNicknameOk] = useState<boolean | null>(null);
    const [password, setPassword] = useState('');
    const [password2, setPassword2] = useState('');
    const [lolTag, setLolTag] = useState('');
    const [lolTagOk, setLolTagOk] = useState<boolean | null>(null);
    const [submitting, setSubmitting] = useState(false);

    const handleNext = () => setActiveStep((s) => s + 1);
    const handleBack = () => setActiveStep((s) => s - 1);

    const onRequestEmail = async () => {
        await requestEmailCode(email);
        setEmailRequested(true);
    };

    const onVerifyEmail = async () => {
        await verifyEmailCode(email, emailCode);
        setEmailVerified(true);
        handleNext();
    };

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
            await registerUser({
                email,
                nickname,
                password,
                lolTag,
                agreements,
            });
            setActiveStep(steps.length);
        } finally {
            setSubmitting(false);
        }
    };

    const stepKey = steps[activeStep]?.key;

    return (
        <Box>
            <Stepper activeStep={activeStep} alternativeLabel>
                {steps.map((s) => (
                    <Step key={s.key}>
                        <StepLabel>{s.label}</StepLabel>
                    </Step>
                ))}
            </Stepper>

            <Box mt={4}>
                {stepKey === 'agreements' && (
                    <Stack spacing={1}>
                        <Typography variant="body2">서비스 이용을 위해 필수 약관에 동의해 주세요.</Typography>
                        <FormControlLabel control={<Checkbox checked={agreements.terms} onChange={(_, v) => setAgreements(a => ({ ...a, terms: v }))} />} label="이용약관 동의 (필수)" />
                        <FormControlLabel control={<Checkbox checked={agreements.privacy} onChange={(_, v) => setAgreements(a => ({ ...a, privacy: v }))} />} label="개인정보 처리방침 동의 (필수)" />
                        <FormControlLabel control={<Checkbox checked={agreements.marketing} onChange={(_, v) => setAgreements(a => ({ ...a, marketing: v }))} />} label="마케팅 수신 동의 (선택)" />
                        <Box display="flex" justifyContent="flex-end" mt={2}>
                            <Button variant="contained" onClick={handleNext} disabled={!agreements.terms || !agreements.privacy}>다음</Button>
                        </Box>
                    </Stack>
                )}

                {stepKey === 'email' && (
                    <Stack spacing={2}>
                        <TextField label="이메일" value={email} onChange={(e) => setEmail(e.target.value)} fullWidth />
                        <Box display="flex" justifyContent="space-between">
                            <Button onClick={handleBack}>이전</Button>
                            <Button variant="contained" onClick={onRequestEmail} disabled={!email}>인증 코드 전송</Button>
                        </Box>
                        {emailRequested && <Typography variant="caption">인증 코드가 전송되었습니다. 메일함을 확인하세요.</Typography>}
                        <Box display="flex" justifyContent="flex-end">
                            <Button variant="outlined" onClick={handleNext} disabled={!emailRequested}>다음</Button>
                        </Box>
                    </Stack>
                )}

                {stepKey === 'emailVerify' && (
                    <Stack spacing={2}>
                        <TextField label="인증 코드" value={emailCode} onChange={(e) => setEmailCode(e.target.value)} fullWidth />
                        <Box display="flex" justifyContent="space-between">
                            <Button onClick={handleBack}>이전</Button>
                            <Button variant="contained" onClick={onVerifyEmail} disabled={!emailCode}>인증하기</Button>
                        </Box>
                        {emailVerified && <Typography color="success.main">인증이 완료되었습니다.</Typography>}
                    </Stack>
                )}

                {stepKey === 'nickname' && (
                    <Stack spacing={2}>
                        <TextField label="닉네임" value={nickname} onChange={(e) => setNickname(e.target.value)} fullWidth />
                        <Button variant="outlined" onClick={onCheckNickname} disabled={!nickname}>중복 확인</Button>
                        {nicknameOk === true && <Typography color="success.main">사용 가능한 닉네임입니다.</Typography>}
                        {nicknameOk === false && <Typography color="error.main">이미 사용 중인 닉네임입니다.</Typography>}
                        <Box display="flex" justifyContent="space-between">
                            <Button onClick={handleBack}>이전</Button>
                            <Button variant="contained" onClick={handleNext} disabled={!nicknameOk}>다음</Button>
                        </Box>
                    </Stack>
                )}

                {stepKey === 'password' && (
                    <Stack spacing={2}>
                        <TextField label="비밀번호" type="password" value={password} onChange={(e) => setPassword(e.target.value)} fullWidth />
                        <TextField label="비밀번호 확인" type="password" value={password2} onChange={(e) => setPassword2(e.target.value)} fullWidth />
                        <Box display="flex" justifyContent="space-between">
                            <Button onClick={handleBack}>이전</Button>
                            <Button variant="contained" onClick={handleNext} disabled={!password || password !== password2}>다음</Button>
                        </Box>
                    </Stack>
                )}

                {stepKey === 'loltag' && (
                    <Stack spacing={2}>
                        <TextField label="롤 태그 (예: Summoner#KR1)" value={lolTag} onChange={(e) => setLolTag(e.target.value)} fullWidth />
                        <Button variant="outlined" onClick={onCheckLolTag} disabled={!lolTag}>중복 확인</Button>
                        {lolTagOk === true && <Typography color="success.main">사용 가능한 태그입니다.</Typography>}
                        {lolTagOk === false && <Typography color="error.main">이미 사용 중인 태그입니다.</Typography>}
                        <Box display="flex" justifyContent="space-between">
                            <Button onClick={handleBack}>이전</Button>
                            <Button variant="contained" onClick={onSubmit} disabled={!lolTagOk || submitting}>{submitting ? '가입 중...' : '가입하기'}</Button>
                        </Box>
                    </Stack>
                )}

                {activeStep >= steps.length && (
                    <Box textAlign="center">
                        <Typography variant="h6" gutterBottom>회원가입이 완료되었습니다.</Typography>
                        <Typography variant="body2">로그인 페이지로 이동해 주세요.</Typography>
                    </Box>
                )}
            </Box>
        </Box>
    );
};

export default RegisterWizard;



