// frontend/src/components/layout/Sidebar.tsx
'use client';
import React from 'react';
import Link from 'next/link';
import styles from './Sidebar.module.scss';
import { useAuthStore } from '@/stores/authStore';

const Sidebar = () => {
    const { isAuthenticated, username, logout } = useAuthStore();

    return (
        <aside className={styles.sidebar}>
            <nav className={styles.nav}>
                <Link href="/" className={styles.navItem}>홈</Link>
                <Link href="/lobbies" className={styles.navItem}>로비 목록</Link>
                {/*
        <Link href="/community" className={styles.navItem}>커뮤니티</Link>
        <Link href="/records" className={styles.navItem}>전적</Link>
        */}
            </nav>
            <div className={styles.profile}>
                {isAuthenticated ? (
                    <>
                        <p>{username}님 환영합니다!</p>
                        <button onClick={logout} className={styles.logoutButton}>로그아웃</button>
                    </>
                ) : (
                    <>
                        <p>로그인이 필요합니다.</p>
                        <Link href="/login" className={styles.navItem}>로그인하기</Link>
                    </>
                )}
            </div>
        </aside>
    );
};

export default Sidebar;