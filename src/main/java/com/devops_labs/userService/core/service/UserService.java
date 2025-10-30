package com.devops_labs.userService.core.service;

import com.devops_labs.userService.api.dto.CreateUserRequest;
import com.devops_labs.userService.api.dto.UserResponse;
import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .email(request.email())
                .build();
        user = userRepository.save(user);

        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getStatus());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getStatus());
    }
}
