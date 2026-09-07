package com.kgs.homeslot.module.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyInquiryDto {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long builderId;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private String status;
    private String replyMessage;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;
}
