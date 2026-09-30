package com.kgs.homeslot.module.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long senderId;
    private String senderEmail;
    private String senderName;
    private Long recipientId;
    private String recipientEmail;
    private String recipientName;
    private Long propertyId;
    private String propertyTitle;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
