package com.strongcom.doormate.repository;

import com.strongcom.doormate.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 사용자 정보 조회 및 권한 목록 조회
     *
     * @param username - 사용자 ID
     * @return 사용자
     */

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByKakaoId(Long id);

}

