package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.user.UpdateProfileRequest;
import com.shahid.ecommerce.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse updateCurrentUser(UpdateProfileRequest request);

    Page<UserResponse> findAll(Pageable pageable);
}
