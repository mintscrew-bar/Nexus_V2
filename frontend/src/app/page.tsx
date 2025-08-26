'use client'; // 이 컴포넌트가 클라이언트 측에서 실행되도록 설정합니다.

import { useEffect } from 'react';
import Link from 'next/link'; // Next.js의 페이지 이동을 위한 Link 컴포넌트
import { useAuthStore } from '@/stores/authStore'; // 절대 경로를 사용하여 authStore를 가져옵니다.

export default function Home() {
  // authStore에서 필요한 상태와 함수들을 직접 가져옵니다.
  const { isAuthenticated, username, initialize, login } = useAuthStore();

  // 앱이 처음 실행될 때 한 번만 Keycloak 초기화 함수를 호출합니다.
  useEffect(() => {
    initialize();
  }, [initialize]);

  return (
    <main style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh', textAlign: 'center' }}>
      <h1>Nexus에 오신 것을 환영합니다!</h1>
      <p>리그 오브 레전드 내전 플랫폼</p>
      <div style={{ marginTop: '20px' }}>
        {isAuthenticated ? (
          <div>
            <p>환영합니다, {username}!</p>
            <Link href="/lobbies" style={{ display: 'inline-block', padding: '10px 20px', marginTop: '10px', backgroundColor: '#0070f3', color: 'white', textDecoration: 'none', borderRadius: '5px' }}>
              로비로 이동하기
            </Link>
          </div>
        ) : (
          <div>
            <p>서비스를 이용하려면 로그인이 필요합니다.</p>
            <button onClick={login} style={{ padding: '10px 20px', cursor: 'pointer', fontSize: '16px' }}>
              Login
            </button>
          </div>
        )}
      </div>
    </main>
  );
}