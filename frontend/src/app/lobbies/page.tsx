// frontend/src/app/lobbies/page.tsx

'use client';

import { useState, useEffect,  useCallback } from 'react';
// createGameRoom을 api 서비스에서 가져옵니다.
import { getGameRooms, createGameRoom } from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import Link from 'next/link';
// Button과 Box 컴포넌트를 import합니다.
import { Button, Box, Grid, Card, CardContent, Typography, Chip, Snackbar, Alert, CircularProgress } from "@mui/material";
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
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // 방 목록을 불러오는 함수를 분리하여 재사용성을 높입니다.
    const fetchRooms = useCallback (async () => {
        if (!isAuthenticated) return;
        try {
            setLoading(true);
            const data = await getGameRooms();
            if (data) {
                setRooms(data);
            } else {
                setRooms([]);
            }
        } catch (error) {
            console.error('게임 방 목록을 불러오는데 실패했습니다:', error);
            setError('게임 방 목록을 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    }, [isAuthenticated]);

    useEffect(() => {
        fetchRooms().catch(console.error);
    }, [fetchRooms]);

    // 새로운 방을 만드는 함수
    const handleCreateRoom = async (title: string, maxParticipants: number) => {
        try {
            await createGameRoom(title, maxParticipants);
            await fetchRooms(); // 방 생성 후 목록을 새로고침합니다.
        } catch (error) {
            console.error('방 생성에 실패했습니다:', error);
            setError('방 생성에 실패했습니다.');
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

            {loading ? (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <CircularProgress size={18} />
                    <Typography color="text.secondary">불러오는 중...</Typography>
                </Box>
            ) : rooms.length === 0 ? (
                <Typography color="text.secondary">생성된 게임 방이 없습니다.</Typography>
            ) : (
                <Grid container spacing={2}>
                    {rooms.map((room) => (
                        <Grid item xs={12} sm={6} md={4} key={room.roomCode}>
                            <Link href={`/lobbies/${room.roomCode}`} style={{ textDecoration: 'none' }}>
                                <Card variant="outlined">
                                    <CardContent>
                                        <Box display="flex" alignItems="center" justifyContent="space-between">
                                            <Typography variant="h6">{room.title}</Typography>
                                            <Chip label={room.status} color={room.status === 'WAITING' ? 'success' : 'default'} size="small" />
                                        </Box>
                                        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                            인원: {room.currentParticipants}/{room.maxParticipants}
                                        </Typography>
                                        <Typography variant="caption" color="text.secondary">방장: {room.hostName}</Typography>
                                    </CardContent>
                                </Card>
                            </Link>
                        </Grid>
                    ))}
                </Grid>
            )}

            <Snackbar open={Boolean(error)} autoHideDuration={3000} onClose={() => setError(null)}>
                <Alert severity="error" onClose={() => setError(null)} sx={{ width: '100%' }}>
                    {error}
                </Alert>
            </Snackbar>
        </div>
    );
}