import { useAuthStore } from '@/stores/authStore';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

type Json = Record<string, unknown> | null;

const jsonFetch = async (path: string, options: RequestInit = {}): Promise<Json> => {
    const token = useAuthStore.getState().token || undefined;
    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        ...(options.headers as Record<string, string> | undefined),
    };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });
    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Request failed: ${res.status}`);
    }
    const text = await res.text();
    return text ? JSON.parse(text) : null;
};

export const oauthRedirect = (provider: 'google' | 'discord') => {
    // Spring Security OAuth2 redirect URL
    window.location.href = `${BASE_URL}/oauth2/authorization/${provider}`;
};

export const requestEmailCode = (email: string) =>
    jsonFetch('/api/auth/email/code', { method: 'POST', body: JSON.stringify({ email }) });

export const verifyEmailCode = (email: string, code: string) =>
    jsonFetch('/api/auth/email/verify', { method: 'POST', body: JSON.stringify({ email, code }) });

export const checkNickname = (nickname: string) =>
    jsonFetch(`/api/auth/check/nickname`, { method: 'POST', body: JSON.stringify({ nickname }) });

export const checkLolTag = (lolTag: string) =>
    jsonFetch(`/api/auth/check/loltag`, { method: 'POST', body: JSON.stringify({ lolTag }) });

export interface RegisterPayload {
    email: string;
    nickname: string;
    password: string;
    lolTag: string;
    agreements: {
        terms: boolean;
        privacy: boolean;
        marketing?: boolean;
    };
}

export const registerUser = (payload: RegisterPayload) =>
    jsonFetch('/api/auth/register', { method: 'POST', body: JSON.stringify(payload) });

export const completeOnboarding = (payload: { nickname: string; lolTag: string }) =>
    jsonFetch('/api/auth/onboarding', { method: 'POST', body: JSON.stringify(payload) });

export interface UserProfile {
    id?: number;
    email?: string;
    nickname?: string;
    summonerName?: string;
}

export const syncMyUser = async (): Promise<UserProfile | null> => {
    try {
        const data = await jsonFetch('/api/users/me', { method: 'GET' });
        return (data as UserProfile) || null;
    } catch (e) {
        return null;
    }
};


