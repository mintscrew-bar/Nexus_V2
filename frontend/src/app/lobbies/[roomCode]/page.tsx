'use client';

import React, { useEffect, useState } from 'react';
import { getGameRoomDetails } from '@/services/api';
import { GameRoomDetails, Participant } from '@/types/gameRoom';
import { useAuthStore } from '@/stores/authStore';
import styles from './GameRoomDetailPage.module.scss';

// GridLegacy 대신 Grid로 변경
import { Card, CardContent, Typography, Button, List, ListItem, ListItemText, CircularProgress, Chip, Paper, Grid } from '@mui/material';

const GameRoomDetailPage = ({ params }: { params: { roomCode: string } }) => {
    const [room, setRoom] = useState<GameRoomDetails | null>(null);
    const [loading, setLoading] = useState(true);
    const { username } = useAuthStore();
    const { roomCode } = params;

    useEffect(() => {
        const fetchRoomDetails = async () => {
            try {
                setLoading(true);
                const data = await getGameRoomDetails(roomCode);
                setRoom(data);
            } catch (error) {
                console.error("Failed to fetch room details:", error);
            } finally {
                setLoading(false);
            }
        };

        if (roomCode) {
            fetchRoomDetails().catch(console.error);
        }
    }, [roomCode]);

    if (loading) {
        return <div className={styles.centered}><CircularProgress /></div>;
    }

    if (!room) {
        return <div className={styles.centered}><Typography variant="h6">방을 찾을 수 없습니다.</Typography></div>;
    }

    const isHost = room.hostName === username;

    return (
        <div className={styles.container}>
            <Paper elevation={3} className={styles.header}>
                <Typography variant="h4" component="h1">{room.title}</Typography>
                <Chip label={room.status} color={room.status === 'WAITING' ? 'success' : 'default'} />
                <Typography variant="body1">
                    인원: {room.currentParticipants} / {room.maxParticipants}
                </Typography>
                <Typography variant="caption" color="textSecondary">
                    방장: {room.hostName}
                </Typography>
            </Paper>

            {/* 새로운 Grid 컨테이너, 아이템 방식 적용 */}
            <Grid container spacing={3}>
                {/* 각 아이템에는 item prop 필요 없음, xs/md는 size prop으로 지정 */}
                <Grid size={{ xs: 12, md: 8 }}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">참가자 목록</Typography>
                            <List>
                                {Array.isArray(room.participants) && room.participants.map((p: Participant) => (
                                    <ListItem key={p.nickname}>
                                        <ListItemText primary={p.nickname} secondary={p.summonerName || '소환사명 미등록'} />
                                    </ListItem>
                                ))}
                            </List>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid size={{ xs: 12, md: 4 }}>
                    <Card>
                        <CardContent className={styles.actions}>
                            <Typography variant="h6">게임 관리</Typography>
                            {isHost ? (
                                <>
                                    <Button variant="contained" color="primary" fullWidth>팀 구성 시작</Button>
                                    <Button variant="contained" color="secondary" fullWidth>게임 시작</Button>
                                </>
                            ) : (
                                <Button variant="contained" color="primary" fullWidth>나가기</Button>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </div>
    );
};

export default GameRoomDetailPage;
