package com.nexus.repository;

import com.nexus.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    // roomCode로 GameRoom을 찾기 위한 메서드
    Optional<GameRoom> findByRoomCode(String roomCode);
}