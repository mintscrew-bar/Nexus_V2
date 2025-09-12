'use client';

import React, { useEffect, useState, useCallback } from 'react';
import { getGameRoomDetails, joinGameRoom, startTeamComposition } from '@/services/api';
import { GameRoomDetails, Participant } from '@/types/gameRoom';
import { useAuthStore } from '@/stores/authStore';
import styles from './GameRoomDetailPage.module.scss';
import { useRouter } from "next/navigation";

import { Card, CardContent, Typography, Button, List, ListItem, ListItemText, CircularProgress, Chip, Paper, Grid } from '@mui/material';

const GameRoomDetailPage = ({ params }: { params: { roomCode: string } }) => {
    const [room, setRoom] = useState<GameRoomDetails | null>(null);
    const [loading, setLoading] = useState(true);
    const { username } = useAuthStore();
    const { roomCode } = params;
    const router = useRouter(); // router는 나중에 페이지 이동 등에 사용할 수 있으므로 유지합니다.

    // useCallback으로 함수를 감싸 불필요한 재성성을 방지합니다.
    const fetchRoomDetails = useCallback(async () => {
        try {
            const data = await getGameRoomDetails(roomCode);
            setRoom(data);
        } catch (error) {
            console.error("방 상세 정보를 불러오는데 실패했습니다:", error);
        } finally {
            setLoading(false);
        }
    }, [roomCode]);

    useEffect(() => {
        if (roomCode) {
            // void 연산자를 사용하여 프로미스를 의도적으로 무시함을 명시합니다.
            // 이것이 이 경고를 해결하는 가장 깔끔한 방법입니다.
            void fetchRoomDetails();
        }
    }, [roomCode, fetchRoomDetails]);

    const handleJoinRoom = async () => {
        try {
            await joinGameRoom(roomCode);
            alert('방에 참가했습니다!');
            await fetchRoomDetails();
        } catch (error) {
            alert('방 참가에 실패했습니다. 이미 참가했거나 방이 가득 찼을 수 있습니다.');
        }
    };

    const handleLeaveRoom = () => {
        alert('나가기 API는 아직 구현되지 않았습니다.');
    };

    const handleStartTeamComposition = async () => {
        const method = prompt("팀 구성 방식을 입력하세요 (AUTO 또는 AUCTION):");
        if (method && (method.toUpperCase() === 'AUTO' || method.toUpperCase() === 'AUCTION')) {
            try {
                await startTeamComposition(roomCode, method.toUpperCase() as 'AUTO' | 'AUCTION');
                alert('팀 구성이 시작되었습니다!');
                await fetchRoomDetails();
            } catch (error) {
                alert('팀 구성 시작에 실패했습니다.');
            }
        } else if (method !== null) {
            alert('잘못된 방식입니다. AUTO 또는 AUCTION 중 하나를 입력해주세요.');
        }
    };

    if (loading) {
        return <div className={styles.centered}><CircularProgress /></div>;
    }

    if (!room) {
        return <div className={styles.centered}><Typography variant="h6">방을 찾을 수 없습니다.</Typography></div>;
    }

    const isHost = room.hostName === username;
    const isParticipant = Array.isArray(room.participants) && room.participants.some(p => p.nickname === username);

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

            {/* 보내주신 Grid 구조를 그대로 유지합니다. */}
            <Grid container spacing={3}>
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
                                    <Button variant="contained" color="primary" fullWidth onClick={handleStartTeamComposition}>팀 구성 시작</Button>
                                    <Button variant="contained" color="secondary" fullWidth>게임 시작</Button>
                                </>
                            ) : isParticipant ? (
                                <Button variant="contained" color="error" fullWidth onClick={handleLeaveRoom}>나가기</Button>
                            ) : (
                                <Button variant="contained" color="primary" fullWidth onClick={handleJoinRoom}>참가하기</Button>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </div>
    );
};

export default GameRoomDetailPage;