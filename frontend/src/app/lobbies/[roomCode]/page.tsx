'use client';

import { useState, useEffect, useCallback } from 'react';
import { useAuthStore } from '@/stores/authStore';
import { getGameRoomByCode } from '@/services/api';
import { GameRoom, Participant } from '@/types/gameRoom';
import styles from './GameRoomDetailPage.module.scss';

import { Paper, Typography, Card, CardContent, List, ListItem, ListItemText, CircularProgress, Button } from '@mui/material';
import Grid from '@mui/material/Grid';

// 1. 페이지 props를 위한 올바른 인터페이스를 정의합니다.
interface GameRoomDetailPageProps {
    params: {
        roomCode: string;
    };
}

// 2. 컴포넌트가 위 인터페이스를 props 타입으로 받도록 명시합니다.
const GameRoomDetailPage = ({ params }: GameRoomDetailPageProps) => {
    const { roomCode } = params;
    const { isAuthenticated, username } = useAuthStore();
    const [room, setRoom] = useState<GameRoom | null>(null);
    const [loading, setLoading] = useState(true);

    const fetchRoomDetails = useCallback(async () => {
        if (!isAuthenticated || !roomCode) return;
        try {
            setLoading(true);
            const data = await getGameRoomByCode(roomCode as string);
            setRoom(data);
        } catch (error) {
            console.error('게임 방 정보를 불러오는데 실패했습니다:', error);
            setRoom(null); // 에러 발생 시 room 정보를 초기화
        } finally {
            setLoading(false);
        }
    }, [isAuthenticated, roomCode]);

    useEffect(() => {
        const loadRoomDetails = async () => {
            await fetchRoomDetails();
        };
        loadRoomDetails().catch(console.error);
    }, [fetchRoomDetails]);

    if (loading) {
        return <CircularProgress />;
    }

    if (!room) {
        return <Typography>존재하지 않거나 참여할 수 없는 방입니다.</Typography>;
    }

    const isHost = room.hostName === username;
    const isParticipant = Array.isArray(room.participants) && room.participants.some(p => p.nickname === username);

    return (
        <div className={styles.container}>
            <Paper elevation={3} className={styles.header}>
                <Typography variant="h4" component="h1">{room.title}</Typography>
                <Typography variant="body1" color={room.status === 'WAITING' ? 'success' : 'default'}>
                    상태: {room.status}
                </Typography>
                <Typography variant="body1">
                    인원: {room.currentParticipants} / {room.maxParticipants}
                </Typography>
                <Typography variant="caption" color="textSecondary">
                    방장: {room.hostName}
                </Typography>
            </Paper>

            <Grid container spacing={3}>
                <Grid item xs={12} md={8}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">참가자</Typography>
                            <List>
                                {Array.isArray(room.participants) && room.participants.map((p) => (
                                    <ListItem key={p.nickname}>
                                        <ListItemText primary={p.nickname} secondary={p.summonerName || '소환사명 없음'} />
                                    </ListItem>
                                ))}
                            </List>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent className={styles.actions}>
                            <Typography variant="h6">팀 구성/매칭</Typography>
                            {isHost && <Button variant="contained">팀 구성 시작</Button>}
                            {!isParticipant && <Button variant="contained" color="primary">참가하기</Button>}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </div>
    );
};

export default GameRoomDetailPage;