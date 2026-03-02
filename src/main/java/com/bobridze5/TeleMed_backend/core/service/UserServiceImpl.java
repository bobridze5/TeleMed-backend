package com.bobridze5.TeleMed_backend.core.service;

import com.bobridze5.TeleMed_backend.api.dto.data.ChangeUserDataRequest;
import com.bobridze5.TeleMed_backend.api.dto.data.ChangeUserDataResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UserResponse;
import com.bobridze5.TeleMed_backend.api.dto.users.UsersResponse;
import com.bobridze5.TeleMed_backend.core.entity.auth.User;
import com.bobridze5.TeleMed_backend.core.entity.auth.UserStatus;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.exceptions.RequestParamInvalidException;
import com.bobridze5.TeleMed_backend.core.repository.UserRepository;
import com.bobridze5.TeleMed_backend.core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenStoreServiceImpl refreshTokenStoreService;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id = " + id + " not found")
        );
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getStatus()
        );
    }

    public UsersResponse getUsers(Integer pageNumber) {
        if (pageNumber == null || pageNumber < 0) {
            throw new RequestParamInvalidException("Request parameter page must be int and >= 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by("id"));
        List<User> users = userRepository.findAll(pageable).toList();

        List<UserResponse> data = users.stream().map(user -> new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getStatus()
        )).toList();

        return new UsersResponse(data);
    }

    @Transactional
    public ChangeUserDataResponse changeUserData(Long id, ChangeUserDataRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Access denied");
        }

        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }


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

        if (request.status() != null) {
            user.setStatus(request.status());
        }

        user = userRepository.save(user);

        return new ChangeUserDataResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getStatus()
        );
    }

    @Transactional
    public void deleteUserById(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Access denied");
        }

        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " not found"));

        user.setStatus(UserStatus.INACTIVE);
        user = userRepository.save(user);
        refreshTokenStoreService.delete(user.getId());
    }

}
