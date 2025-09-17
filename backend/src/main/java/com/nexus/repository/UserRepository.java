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

    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserCode(String userCode); 
    Optional<User> findByNickname(String nickname);
}
