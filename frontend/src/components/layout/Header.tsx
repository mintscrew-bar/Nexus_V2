// frontend/src/components/layout/Header.tsx
import React from 'react';
import styles from './Header.module.scss';

const Header = () => {
    return (
        <header className={styles.header}>
            <div className={styles.logo}>Nexus</div>
            {/* 검색창 등 추가될 수 있는 공간 */}
        </header>
    );
};

export default Header;