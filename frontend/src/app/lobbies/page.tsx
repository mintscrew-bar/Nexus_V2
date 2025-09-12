// frontend/src/app/lobbies/page.tsx

'use client';

import { useState, useEffect } from 'react';
// createGameRoom을 api 서비스에서 가져옵니다.
import { getGameRooms, createGameRoom } from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import Link from 'next/link';
// Button과 Box 컴포넌트를 import합니다.
import { Button, Box } from "@mui/material";
// 방 만들기 모달 컴포넌트를 import합니다.
import CreateRoomModal from "@/components/CreateRoomModal";

interface GameRoom {
    roomCode: string;
    title: string;
    maxParticipants: number;
    currentParticipants: number;
    hostName: string;
    status: string;
}

export default function LobbiesPage() {
    const [rooms, setRooms] = useState<GameRoom[]>([]);
    const { isAuthenticated } = useAuthStore();
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달의 열림/닫힘 상태

    // 방 목록을 불러오는 함수를 분리하여 재사용성을 높입니다.
    const fetchRooms = async () => {
        if (!isAuthenticated) return;
        try {
            const data = await getGameRooms();
            if (data) {
                setRooms(data);
            }
        } catch (error) {
            console.error('게임 방 목록을 불러오는데 실패했습니다:', error);
        }
    };

    useEffect(() => {
        fetchRooms().catch(console.error);
    }, [isAuthenticated]);

    // 새로운 방을 만드는 함수
    const handleCreateRoom = async (title: string, maxParticipants: number) => {
        try {
            await createGameRoom(title, maxParticipants);
            await fetchRooms(); // 방 생성 후 목록을 새로고침합니다.
        } catch (error) {
            console.error('방 생성에 실패했습니다:', error);
            // 여기에 사용자에게 에러 알림을 보여주는 로직을 추가할 수 있습니다.
        }
    };

    if (!isAuthenticated) {
        return <p>로비 목록을 보려면 로그인이 필요합니다.</p>;
    }

    return (
        <div>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <h2>게임 로비</h2>
                <Button variant="contained" onClick={() => setIsModalOpen(true)}>
                    방 만들기
                </Button>
            </Box>

            <CreateRoomModal
                open={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onCreate={handleCreateRoom}
            />

            {rooms.length === 0 ? (
                <p>생성된 게임 방이 없습니다.</p>
            ) : (
                <ul style={{ listStyle: 'none', padding: 0 }}>
                    {rooms.map((room) => (
                        <li key={room.roomCode} style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '10px' }}>
                            <Link href={`/lobbies/${room.roomCode}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                                <h3>{room.title}</h3>
                                <p>상태: {room.status} | 인원: {room.currentParticipants}/{room.maxParticipants}</p>
                                <p>방장: {room.hostName}</p>
                            </Link>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}