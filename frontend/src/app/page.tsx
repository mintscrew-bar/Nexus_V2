'use client'; // 이 컴포넌트가 클라이언트 측에서 실행되도록 설정합니다.

import { useState, useEffect, useRef } from 'react';
import Keycloak from 'keycloak-js';

// Keycloak 인스턴스를 저장할 변수
let keycloakInstance: Keycloak | null = null;

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState('');
  const isRun = useRef(false); // useEffect가 두 번 실행되는 것을 방지합니다.

  useEffect(() => {
    // Strict Mode에서 컴포넌트가 두 번 렌더링될 때, Keycloak 초기화가 한 번만 실행되도록 합니다.
    if (isRun.current) return;
    isRun.current = true;

    // Keycloak 인스턴스를 생성하고 설정합니다.
    const keycloak: Keycloak = new Keycloak({
      url: 'http://localhost:8180', // Docker Compose에서 설정한 Keycloak 주소
      realm: 'nexus',               // 우리가 생성한 Realm 이름
      clientId: 'nexus-frontend',   // 우리가 생성한 Client ID
    });

    keycloakInstance = keycloak;

    // Keycloak을 초기화합니다.
    keycloak.init({ onLoad: 'check-sso' })
      .then(authenticated => {
        setIsAuthenticated(authenticated);
        if (authenticated && keycloak.tokenParsed) {
          // 'preferred_username'은 Keycloak에서 기본으로 제공하는 사용자 이름입니다.
          setUsername(keycloak.tokenParsed.preferred_username || 'Unknown User');
        }
      })
      .catch(error => {
        console.error("Keycloak 초기화 실패", error);
        setIsAuthenticated(false);
      });

  }, []); // 빈 배열을 전달하여 컴포넌트가 처음 마운트될 때 한 번만 실행되도록 합니다.

  const handleLogin = () => {
    keycloakInstance?.login();
  };

  const handleLogout = () => {
    keycloakInstance?.logout();
  };

  return (
    <main style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh' }}>
      <h1>Nexus Project</h1>
      {isAuthenticated ? (
        <div>
          <p>환영합니다, {username}!</p>
          <button onClick={handleLogout} style={{ padding: '10px 20px', cursor: 'pointer' }}>
            Logout
          </button>
        </div>
      ) : (
        <div>
          <p>로그인이 필요합니다.</p>
          <button onClick={handleLogin} style={{ padding: '10px 20px', cursor: 'pointer' }}>
            Login
          </button>
        </div>
      )}
    </main>
  );
}
