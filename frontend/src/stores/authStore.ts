// frontend/src/stores/authStore.ts
import { create } from 'zustand';

interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  username: string;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  setAuth: (token: string, username?: string) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  token: null,
  username: '',

  login: async (email: string, password: string) => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'}/api/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const apiResponse = await response.json();

      if (!apiResponse.success) {
        throw new Error(apiResponse.error || 'Login failed');
      }

      set({
        isAuthenticated: true,
        token: apiResponse.data.token,
        username: apiResponse.data.username
      });
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },

  logout: () => {
    set({ isAuthenticated: false, token: null, username: '' });
  },

  setAuth: (token: string, username?: string) => {
    set({ isAuthenticated: true, token, username: username || '' });
  },
}));