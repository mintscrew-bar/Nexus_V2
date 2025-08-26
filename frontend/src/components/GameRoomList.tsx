// frontend/src/components/GameRoomList.tsx
'use client';

import { useState, useEffect } from 'react';
import { getGameRooms } from '@/services/api'; // 상대 경로('../')를 절대 경로('@/')로 수정
import { useAuthStore } from '@/stores/authStore'; // 절대 경로로 수정

interface GameRoom {
  roomCode: string;
  title: string;
  maxParticipants: number;
  currentParticipants: number;
  hostName: string;
  status: string;
}

export default function GameRoomList() {
  const [rooms, setRooms] = useState<GameRoom[]>([]);
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (isAuthenticated) {
      const fetchRooms = async () => {
        try {
          const data = await getGameRooms();
          // data가 null이 아닐 경우에만 상태를 업데이트합니다.
          if (data) {
            setRooms(data);
          }
        } catch (error) {
          console.error('게임 방 목록을 불러오는데 실패했습니다:', error);
        }
      };
      fetchRooms();
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) {
    return <p>게임 목록을 보려면 로그인이 필요합니다.</p>;
  }

  return (
    <div>
      <h2>게임 로비</h2>
      {rooms.length === 0 ? (
        <p>생성된 게임 방이 없습니다.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {rooms.map((room) => (
            <li key={room.roomCode} style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '10px' }}>
              <h3>{room.title}</h3>
              <p>상태: {room.status} | 인원: {room.currentParticipants}/{room.maxParticipants}</p>
              <p>방장: {room.hostName}</p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}