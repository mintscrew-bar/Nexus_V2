'use client';

import React, { useState, useEffect, useCallback } from 'react';
import { useAuthStore } from '@/stores/authStore';
import { getGameRoomByCode, startTeamComposition, joinGameRoom } from '@/services/api';
import { GameRoom, Participant } from '@/types/gameRoom';
import styles from './GameRoomDetailPage.module.scss';

import { Paper, Typography, Card, CardContent, List, ListItem, ListItemText, CircularProgress, Button, Grid } from '@mui/material';

export interface GameRoomDetailPageProps {
  params: {
    roomCode: string;
  };
}

const GameRoomDetailPage = ({ params }: GameRoomDetailPageProps) => {
    const { roomCode } = params;
    const { isAuthenticated, username } = useAuthStore();
    const [room, setRoom] = useState<GameRoom | null>(null);
    const [loading, setLoading] = useState(true);
    const [joining, setJoining] = useState(false);
    const [starting, setStarting] = useState(false);

    const fetchRoomDetails = useCallback(async () => {
        if (!isAuthenticated || !roomCode) return;
        try {
            setLoading(true);
            const data = await getGameRoomByCode(roomCode as string);
            setRoom(data);
        } catch (error) {
            console.error('게임 방 정보를 불러오는데 실패했습니다:', error);
            setRoom(null);
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
        return <div className={styles.centered}><CircularProgress /></div>;
    }

    if (!room) {
        return <div className={styles.centered}><Typography>존재하지 않거나 참여할 수 없는 방입니다.</Typography></div>;
    }

    const isHost = room.hostName === username;
    const isParticipant = Array.isArray(room.participants) && room.participants.some((p: Participant) => p.nickname === username);

    const handleJoin = async () => {
        try {
            setJoining(true);
            await joinGameRoom(roomCode);
            await fetchRoomDetails();
        } finally {
            setJoining(false);
        }
    };

    const handleStartComposition = async () => {
        try {
            setStarting(true);
            await startTeamComposition(roomCode, 'AUTO');
            await fetchRoomDetails();
        } finally {
            setStarting(false);
        }
    };

    return (
        <div className={styles.container}>
            <Paper elevation={3} className={styles.header}>
                <Typography variant="h4" component="h1">{room.title}</Typography>
                <Typography variant="body1" color={room.status === 'WAITING' ? 'success.main' : 'text.secondary'}>
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
                                {Array.isArray(room.participants) && room.participants.map((p: Participant) => (
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
                            <Typography variant="h6">액션</Typography>
                            {isHost && (
                                <Button variant="contained" onClick={handleStartComposition} disabled={starting} sx={{ mt: 1 }}>
                                    {starting ? '시작 중...' : '팀 구성 시작'}
                                </Button>
                            )}
                            {!isParticipant && (
                                <Button variant="contained" color="primary" onClick={handleJoin} disabled={joining} sx={{ mt: 1 }}>
                                    {joining ? '참가 중...' : '참가하기'}
                                </Button>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </div>
    );
};

export default GameRoomDetailPage;