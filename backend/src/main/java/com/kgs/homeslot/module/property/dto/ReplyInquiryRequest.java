package com.kgs.homeslot.module.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyInquiryRequest {
    @NotBlank(message = "Reply message is required")
    private String replyMessage;
}
