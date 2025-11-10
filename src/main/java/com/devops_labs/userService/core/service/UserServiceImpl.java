package com.devops_labs.userService.core.service;

import com.devops_labs.userService.api.dto.users.UserResponse;
import com.devops_labs.userService.api.dto.data.ChangeUserDataRequest;
import com.devops_labs.userService.api.dto.data.ChangeUserDataResponse;
import com.devops_labs.userService.api.dto.users.UsersResponse;
import com.devops_labs.userService.core.entity.User;
import com.devops_labs.userService.core.entity.UserStatus;
import com.devops_labs.userService.core.exceptions.EntityNotFoundException;
import com.devops_labs.userService.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenStoreServiceImpl refreshTokenStoreService;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getStatus()
        );
    }

    public UsersResponse getUsers(Pageable pageable) {
        List<User> users = userRepository.findAll(pageable).toList();

        List<UserResponse> data = users.stream().map(i -> new UserResponse(
                i.getId(),
                i.getFirstName(),
                i.getLastName(),
                i.getMiddleName(),
                i.getStatus()
        )).toList();

        return new UsersResponse(data);
    }

    @Transactional
    @PreAuthorize("#id == authentication.principal.id")
    public ChangeUserDataResponse changeUserData(Long id, ChangeUserDataRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " not found"));

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.email() != null && !userRepository.existsByEmail(request.email())) {
            user.setEmail(request.email());
        }

        if (request.middleName() != null) {
            user.setMiddleName(request.middleName());
        }

        if (request.password() != null) {
            String password = passwordEncoder.encode(request.password());
            user.setPasswordHash(password);
        }

        if (request.nickname() != null && !userRepository.existsByNickname(request.nickname())) {
            user.setNickname(request.nickname());
        }

        if (request.status() != null) {
            user.setStatus(request.status());
        }

        user = userRepository.save(user);

        return new ChangeUserDataResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getNickname(),
                user.getEmail(),
                user.getStatus()
        );
    }

    @Transactional
    @PreAuthorize("#id == authentication.principal.id")
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id = " + id + " not found");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " not found"));

        user.setStatus(UserStatus.INACTIVE);
        user = userRepository.save(user);
        refreshTokenStoreService.delete(user.getId());
    }

}
