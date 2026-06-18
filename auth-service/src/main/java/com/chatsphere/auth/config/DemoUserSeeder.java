package com.chatsphere.auth.config;

import com.chatsphere.auth.entity.User;
import com.chatsphere.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("demo1", "demo1@test.com", "+1234567890");
        seedUser("demo2", "demo2@test.com", "+0987654321");
    }

    private void seedUser(String username, String email, String phone) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode("123456"))
                    .bio("Hi! I am " + username + ", a demo user in ChatSphere.")
                    .avatarUrl("")
                    .phone(phone)
                    .status("OFFLINE")
                    .enabled(true)
                    .failedLoginAttempts(0)
                    .build();
            userRepository.save(user);
            log.info(">>>> Seeded demo user: {} ({}) with phone: {}", username, email, phone);
        }
    }
}
