package com.kgs.homeslot.module.communication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendChatMessageRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotBlank(message = "Message text is required")
    private String message;
}
