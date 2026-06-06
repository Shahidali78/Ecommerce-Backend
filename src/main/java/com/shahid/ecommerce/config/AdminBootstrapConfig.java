package com.shahid.ecommerce.config;

import com.shahid.ecommerce.model.AppUser;
import com.shahid.ecommerce.model.Role;
import com.shahid.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    ApplicationRunner adminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.password:}") String password,
            @Value("${app.admin.name:Store Administrator}") String name
    ) {
        return args -> {
            if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
                return;
            }
            if (password.length() < 8) {
                throw new IllegalStateException("ADMIN_PASSWORD must contain at least 8 characters");
            }

            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                return;
            }

            AppUser admin = new AppUser();
            admin.setFullName(name.trim());
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        };
    }
}
