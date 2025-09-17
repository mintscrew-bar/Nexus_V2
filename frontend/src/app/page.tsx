'use client';

import Link from 'next/link';
import { Box, Button, Container, Grid, Paper, Stack, Typography, Avatar } from '@mui/material';
import { useAuthStore } from '@/stores/authStore';

export default function Home() {
  const { isAuthenticated, username } = useAuthStore();

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Stack spacing={3}>
        {/* Hero */}
        <Paper
          elevation={0}
          sx={{
            p: 4,
            borderRadius: 3,
            background: 'linear-gradient(135deg, rgba(88,101,242,0.15) 0%, rgba(30,158,255,0.12) 100%)',
            border: '1px solid rgba(255,255,255,0.08)'
          }}
        >
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={3} alignItems={{ xs: 'flex-start', md: 'center' }}>
            <Box flex={1}>
              <Typography variant="h4" sx={{ fontWeight: 700 }} gutterBottom>
                Nexus로 팀을 모으고 내전을 시작하세요
              </Typography>
              <Typography variant="body1" color="text.secondary">
                디스코드처럼 친숙한 UI와 OP.GG 스타일의 정보 구성으로 빠르게 로비를 만들고 팀을 구성할 수 있어요.
              </Typography>
              <Stack direction="row" spacing={1.5} sx={{ mt: 3 }}>
                <Button component={Link} href="/lobbies" variant="contained">
                  로비 둘러보기
                </Button>
                {isAuthenticated ? (
                  <Button component={Link} href="/lobbies" variant="outlined">
                    {username}님, 이어서 진행하기
                  </Button>
                ) : (
                  <Button component={Link} href="/login" variant="outlined">
                    로그인
                  </Button>
                )}
              </Stack>
            </Box>
            <Stack direction="row" spacing={2}>
              <Avatar src="/globe.svg" variant="rounded" sx={{ width: 72, height: 72, bgcolor: 'transparent' }} />
              <Avatar src="/window.svg" variant="rounded" sx={{ width: 72, height: 72, bgcolor: 'transparent' }} />
              <Avatar src="/file.svg" variant="rounded" sx={{ width: 72, height: 72, bgcolor: 'transparent' }} />
            </Stack>
          </Stack>
        </Paper>

        {/* Quick Actions */}
        <Grid container spacing={2}>
          {[
            { title: '로비 만들기', desc: '빠르게 커스텀 방 생성', href: '/lobbies' },
            { title: '팀 구성하기', desc: '밸런스 맞춰 자동 팀 배정', href: '/lobbies' },
            { title: '최근 전적 보기', desc: '매치 기록과 리포트', href: '/lobbies' }
          ].map((item) => (
            <Grid item xs={12} sm={6} md={4} key={item.title}>
              <Paper
                elevation={0}
                sx={{ p: 2.5, height: '100%', borderRadius: 2, backgroundColor: 'background.paper', border: '1px solid rgba(255,255,255,0.06)' }}
              >
                <Typography variant="subtitle1" sx={{ fontWeight: 600 }} gutterBottom>
                  {item.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {item.desc}
                </Typography>
                <Button component={Link} href={item.href} size="small" sx={{ mt: 1.5 }}>
                  이동
                </Button>
              </Paper>
            </Grid>
          ))}
        </Grid>

        {/* Recent Lobbies (placeholder) */}
        <Paper elevation={0} sx={{ p: 2.5, borderRadius: 2, backgroundColor: 'background.paper', border: '1px solid rgba(255,255,255,0.06)' }}>
          <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 1.5 }}>
            <Typography variant="h6">최근 로비</Typography>
            <Button component={Link} href="/lobbies" size="small">
              모두 보기
            </Button>
          </Stack>
          <Typography variant="body2" color="text.secondary">
            최근 로비 데이터가 여기에 표시됩니다.
          </Typography>
        </Paper>
      </Stack>
    </Container>
  );
}