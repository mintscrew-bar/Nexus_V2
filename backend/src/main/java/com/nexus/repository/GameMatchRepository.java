package com.nexus.repository;

import com.nexus.entity.GameMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {
    // 앞으로 GameMatch 관련하여 필요한 쿼리 메서드를 여기에 추가할 수 있습니다.
}