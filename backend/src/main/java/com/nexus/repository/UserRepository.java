package com.nexus.repository;

import com.nexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // JpaRepository를 상속받는 것만으로 기본적인 CRUD 메서드가 자동으로 생성됩니다.
    // 예: save(), findById(), findAll(), delete() 등

    // --- 아래 메서드가 추가되었습니다 ---

    /**
     * Keycloak ID를 사용하여 사용자를 조회합니다.
     * @param keycloakId Keycloak에서 발급한 사용자의 고유 ID
     * @return Optional<User>
     */
    Optional<User> findByKeycloakId(String keycloakId);
}
