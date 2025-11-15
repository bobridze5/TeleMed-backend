package com.devops_labs.userService.core.repository;

import com.devops_labs.userService.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
