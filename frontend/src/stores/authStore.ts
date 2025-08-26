// frontend/src/stores/authStore.ts
import { create } from 'zustand';
import Keycloak from 'keycloak-js';

// 스토어에 저장할 데이터와 함수의 타입을 타입스크립트로 정의합니다.
interface AuthState {
  isAuthenticated: boolean;
  keycloak: Keycloak | null;
  username: string;
  initialize: () => Promise<void>; // Keycloak 초기화 함수
  login: () => void;
  logout: () => void;
}

// Zustand를 사용해 인증 정보 보관소(스토어)를 만듭니다.
export const useAuthStore = create<AuthState>((set, get) => ({
  isAuthenticated: false,
  keycloak: null,
  username: '',

  // Keycloak 인스턴스를 초기화하는 함수
  initialize: async () => {
    // 이 조건문은 서버사이드 렌더링(SSR) 시 에러가 발생하는 것을 방지합니다.
    if (typeof window !== 'undefined') {
      const keycloakInstance = new Keycloak({
        url: 'http://localhost:8180', // 환경 변수로 분리하는 것을 권장합니다.
        realm: 'nexus',
        clientId: 'nexus-frontend',
      });

      try {
        const authenticated = await keycloakInstance.init({ onLoad: 'check-sso' });

        set({
          isAuthenticated: authenticated,
          keycloak: keycloakInstance,
          username: keycloakInstance.tokenParsed?.preferred_username || '',
        });

        // ✨ 로그인 성공 시 백엔드와 사용자 정보 동기화
        if (authenticated) {
          try {
            const response = await fetch('http://localhost:8080/api/users/me', {
              method: 'GET',
              headers: {
                'Authorization': `Bearer ${keycloakInstance.token}`
              }
            });
            
            if (!response.ok) {
              throw new Error('Backend sync failed!');
            }

            const userData = await response.json();
            console.log('✅ Backend user sync successful:', userData);
          } catch (error) {
            console.error('❌ Backend user sync failed:', error);
          }
        }
      } catch (error) {
        console.error("❌ Keycloak initialization failed", error);
        set({ isAuthenticated: false });
      }
    }
  },

  // 로그인 함수
  login: () => {
    get().keycloak?.login();
  },

  // 로그아웃 함수
  logout: () => {
    get().keycloak?.logout();
  },
}));