package com.kgs.homeslot.module.property.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInquiryRequest {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String subject;

    @NotBlank(message = "Message is required")
    private String message;
}
