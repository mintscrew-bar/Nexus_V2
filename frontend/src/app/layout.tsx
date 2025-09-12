// frontend/src/app/layout.tsx

import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.scss";
import AppLayout from "@/components/layout/AppLayout";
import ThemeRegistry from "@/components/ThemeRegistry/ThemeRegistry";
import React from "react"; // 1. ThemeRegistry import

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

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
        <body className={`${geistSans.variable} ${geistMono.variable}`}>
        {/* 2. <ThemeRegistry>로 AppLayout을 감싸줍니다. */}
        <ThemeRegistry>
            <AppLayout>{children}</AppLayout>
        </ThemeRegistry>
        </body>
        </html>
    );
}