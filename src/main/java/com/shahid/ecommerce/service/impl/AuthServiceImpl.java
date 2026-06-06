package com.shahid.ecommerce.service.impl;

import com.shahid.ecommerce.dto.auth.AuthResponse;
import com.shahid.ecommerce.dto.auth.LoginRequest;
import com.shahid.ecommerce.dto.auth.RegisterRequest;
import com.shahid.ecommerce.exception.ConflictException;
import com.shahid.ecommerce.exception.UnauthorizedException;
import com.shahid.ecommerce.model.AppUser;
import com.shahid.ecommerce.model.Role;
import com.shahid.ecommerce.repository.UserRepository;
import com.shahid.ecommerce.security.JwtService;
import com.shahid.ecommerce.service.AuthService;
import com.shahid.ecommerce.service.DtoMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        AppUser saved = userRepository.save(user);

        return responseFor(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (BadCredentialsException exception) {
            throw new UnauthorizedException("Invalid email or password");
        }

        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        return responseFor(user);
    }

    private AuthResponse responseFor(AppUser user) {
        UserDetails details = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        return new AuthResponse(
                jwtService.generateToken(details),
                "Bearer",
                jwtService.getExpirationSeconds(),
                DtoMapper.toUser(user)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
