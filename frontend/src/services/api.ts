// frontend/src/services/api.ts
import { useAuthStore } from '@/stores/authStore'; // 절대 경로로 수정

// API 호출을 위한 기본 fetch 함수
const apiFetch = async (url: string, options: RequestInit = {}) => {
  const token = useAuthStore.getState().keycloak?.token;

  // 헤더의 타입을 명확하게 Record<string, string>으로 지정합니다.
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`http://localhost:8080${url}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    // 에러 발생 시 더 자세한 정보를 포함하도록 수정
    const errorData = await response.text();
    console.error("API Error:", errorData);
    throw new Error(`API request failed with status ${response.status}`);
  }

  // 응답 본문이 비어있을 경우를 처리
  const responseText = await response.text();
  return responseText ? JSON.parse(responseText) : null;
};

// 게임 방 목록을 가져오는 API 함수
export const getGameRooms = () => {
  return apiFetch('/api/games', { method: 'GET' });
};
// 특정 게임 방의 상세 정보를 가져오는 API 함수
export const getGameRoomDetails = (roomCode: string) => {
    return apiFetch(`/api/games/${roomCode}`, { method: 'GET' });
};
/**
* 새로운 게임 방을 생성하는 API 함수
* @param title 방 제목
* @param maxParticipants 최대 참가 인원
*/
export const createGameRoom = (title: string, maxParticipants: number) => {
    return apiFetch('/api/games', {
        method: 'POST',
        body: JSON.stringify({title, maxParticipants}),
    });
};
export const joinGameRoom = (roomCode: string) => {
    return apiFetch(`/api/games/${roomCode}/join`, {
        method: 'POST',
    });
};
export const startTeamComposition = (roomCode: string, method: 'AUTO' | 'AUCTION') => {
    return apiFetch(`/api/games/${roomCode}/team-composition`, {
        method: 'POST',
        body: JSON.stringify({ method }),
    });
};

// roomCode로 특정 게임 방 정보를 가져오는 API 함수
export const getGameRoomByCode = (roomCode: string) => {
    return apiFetch(`/api/games/${roomCode}`, { method: 'GET' });
};