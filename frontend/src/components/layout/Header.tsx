// frontend/src/components/layout/Header.tsx
import React from 'react';
import styles from './Header.module.scss';
import Link from 'next/link';
import { AppBar, Toolbar, Typography, Box, Button } from '@mui/material';
import { useAuthStore } from '@/stores/authStore';

const Header = () => {
    const { isAuthenticated, username, logout } = useAuthStore();
    return (
        <AppBar position="static" color="transparent" elevation={0} className={styles.header}>
            <Toolbar className={styles.toolbar}>
                <Typography variant="h6" component={Link} href="/" className={styles.logo}>
                    Nexus
                </Typography>
                <Box sx={{ flexGrow: 1 }} />
                <Box className={styles.actions}>
                    <Button component={Link} href="/lobbies" color="inherit">로비</Button>
                    {!isAuthenticated && (
                        <Button component={Link} href="/register" color="inherit">회원가입</Button>
                    )}
                    {isAuthenticated ? (
                        <>
                            <Typography variant="body2" sx={{ mx: 1 }}>{username}</Typography>
                            <Button onClick={logout} variant="outlined" color="inherit" size="small">로그아웃</Button>
                        </>
                    ) : (
                        <Button component={Link} href="/login" variant="contained" size="small">로그인</Button>
                    )}
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Header;