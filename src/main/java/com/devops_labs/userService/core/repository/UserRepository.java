package com.devops_labs.userService.core.repository;

import com.devops_labs.userService.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
