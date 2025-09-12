// frontend/src/types/gameRoom.ts

export interface Participant {
    nickname: string;
    summonerName: string | null;
    teamNumber: number | null;
}

export interface GameRoomDetails {
    roomCode: string;
    title: string;
    maxParticipants: number;
    currentParticipants: number;
    hostName: string;
    status: 'WAITING' | 'IN_PROGRESS' | 'COMPLETED' | string;
    createdAt: string; // ISO 8601 날짜 형식의 문자열
    participants: Participant[];
}