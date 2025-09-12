// frontend/src/types/gameRoom.ts

export interface Participant {
    nickname: string;
    summonerName?: string; // 소환사명은 없을 수도 있으므로 optional
    teamNumber?: number;
}

export interface GameRoom {
    roomCode: string;
    title: string;
    maxParticipants: number;
    currentParticipants: number;
    hostName: string;
    status: string;
    participants: Participant[];
}