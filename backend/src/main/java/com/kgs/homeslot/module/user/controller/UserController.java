package com.kgs.homeslot.module.user.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.user.dto.UserProfileDto;
import com.kgs.homeslot.module.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserProfileDto dto = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched successfully", dto));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersForAdmin() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Admin user list fetched successfully", users));
    }
}
