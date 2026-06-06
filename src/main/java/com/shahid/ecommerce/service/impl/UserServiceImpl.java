package com.shahid.ecommerce.service.impl;

import com.shahid.ecommerce.dto.user.UpdateProfileRequest;
import com.shahid.ecommerce.dto.user.UserResponse;
import com.shahid.ecommerce.model.AppUser;
import com.shahid.ecommerce.repository.UserRepository;
import com.shahid.ecommerce.service.CurrentUserService;
import com.shahid.ecommerce.service.DtoMapper;
import com.shahid.ecommerce.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public UserServiceImpl(UserRepository userRepository, CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return DtoMapper.toUser(currentUserService.requireCurrentUser());
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(UpdateProfileRequest request) {
        AppUser user = currentUserService.requireCurrentUser();
        user.setFullName(request.fullName().trim());
        user.setPhone(trimToNull(request.phone()));
        user.setAddress(trimToNull(request.address()));
        return DtoMapper.toUser(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(DtoMapper::toUser);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
