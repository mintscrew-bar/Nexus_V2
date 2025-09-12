// frontend/src/components/layout/AppLayout.tsx
'use client';

import React from 'react';
import Sidebar from './Sidebar';
import Header from './Header';
import styles from './AppLayout.module.scss';

const AppLayout = ({ children }: { children: React.ReactNode }) => {
    return (
        <div className={styles.appLayout}>
            <Sidebar />
            <div className={styles.contentWrapper}>
                <Header />
                <main className={styles.mainContent}>
                    {children}
                </main>
            </div>
        </div>
    );
};

export default AppLayout;