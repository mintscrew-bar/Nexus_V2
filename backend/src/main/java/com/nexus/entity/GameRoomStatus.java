package com.nexus.entity;

public class GameRoomStatus {
    public enum Status {
        WAITING,
        AUCTION_IN_PROGRESS,
        AUTO_TEAM_COMPOSITION,
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }
}
