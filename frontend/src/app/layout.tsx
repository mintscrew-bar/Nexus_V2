// frontend/src/app/layout.tsx

import type { Metadata } from "next";
// 1. next/font/google에서 Inter 폰트를 가져옵니다.
import { Inter } from "next/font/google";
import "./globals.scss";
import AppLayout from "@/components/layout/AppLayout";
import ThemeRegistry from "@/components/ThemeRegistry/ThemeRegistry";
import React from "react";

// 2. Geist 폰트 설정을 Inter 폰트 설정으로 변경합니다.
const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
    title: "Nexus - LoL Custom Platform",
    description: "리그 오브 레전드 내전 플랫폼",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="ko">
        {/* 3. body의 className을 Inter 폰트로 변경합니다. */}
        <body className={inter.className}>
        <ThemeRegistry>
            <AppLayout>{children}</AppLayout>
        </ThemeRegistry>
        </body>
        </html>
    );
}