package com.chatsphere.user.controller;

import com.chatsphere.common.dto.UserDto;
import com.chatsphere.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestBody UserDto updates) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(userService.updateProfile(userId, updates));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<UserDto> updateProfileAlt(
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestBody UserDto updates) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(userService.updateProfile(userId, updates));
    }

    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> uploadProfilePicture(
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestParam("file") MultipartFile file) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(userService.updateProfilePicture(userId, file));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String q) {
        String searchVal = query != null ? query : (q != null ? q : "");
        return ResponseEntity.ok(userService.searchUsers(searchVal));
    }
}
