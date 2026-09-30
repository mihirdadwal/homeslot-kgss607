package com.kgs.homeslot.module.communication.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.communication.dto.NotificationDto;
import com.kgs.homeslot.module.communication.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(Authentication auth) {
        List<NotificationDto> list = notificationService.getUserNotifications(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", list));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication auth) {
        long count = notificationService.getUnreadCount(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Unread notifications count fetched", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id, Authentication auth) {
        notificationService.markAsRead(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
