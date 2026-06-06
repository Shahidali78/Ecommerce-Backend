package com.shahid.ecommerce.service;

import com.shahid.ecommerce.dto.auth.AuthResponse;
import com.shahid.ecommerce.dto.auth.LoginRequest;
import com.shahid.ecommerce.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
