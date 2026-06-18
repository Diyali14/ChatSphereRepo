package com.chatsphere.user.service;

import com.chatsphere.common.dto.UserDto;
import com.chatsphere.common.event.UserActivityEvent;
import com.chatsphere.user.entity.User;
import com.chatsphere.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public UserDto getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    @Transactional
    public UserDto updateProfile(UUID userId, UserDto updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBio(updates.getBio());
        user.setAvatarUrl(updates.getAvatarUrl());
        user.setPhone(updates.getPhone());
        // JPA will automatically check @Version column on save
        userRepository.save(user);

        // Publish profile update event to RabbitMQ
        UserActivityEvent event = UserActivityEvent.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .activityType("PROFILE_UPDATE")
                .status(user.getStatus())
                .details("Profile bio, avatar, or phone updated.")
                .timestamp(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend("message.exchange", "user.activity", event);

        return mapToDto(user);
    }

    @Transactional
    public UserDto updateProfilePicture(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Call media-service to upload the file
        String mediaServiceUrl = "http://media-service/api/media/upload";

        // Construct multipart request
        org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        try {
            org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read upload file bytes", e);
        }

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(body, headers);

        try {
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(mediaServiceUrl, requestEntity, java.util.Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String avatarUrl = (String) response.getBody().get("url");
                user.setAvatarUrl(avatarUrl);
                userRepository.save(user);

                // Publish event
                UserActivityEvent event = UserActivityEvent.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .activityType("PROFILE_UPDATE")
                        .status(user.getStatus())
                        .details("Profile avatar updated via upload.")
                        .timestamp(LocalDateTime.now())
                        .build();
                rabbitTemplate.convertAndSend("message.exchange", "user.activity", event);

                return mapToDto(user);
            } else {
                throw new RuntimeException("Failed to upload image to media-service");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with media-service: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .status(user.getStatus())
                .enabled(user.isEnabled())
                .build();
    }
}
