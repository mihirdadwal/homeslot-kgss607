package com.kgs.homeslot.module.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendEmailVerificationLink(String email, String token) {
        String link = "http://localhost:4200/auth/verify-email?token=" + token + "&email=" + email;
        logger.info("[NOTIFICATION - EMAIL VERIFICATION LINK] Sent to: {}, Link: {}", email, link);
    }

    public void sendOtpEmail(String email, String otpCode) {
        logger.info("[NOTIFICATION - SENDGRID SANDBOX MOCK EMAIL OTP] Sent to: {}, OTP Code: {}", email, otpCode);
    }

    public void sendOtpSms(String phone, String otpCode) {
        logger.info("[NOTIFICATION - TWILIO SANDBOX MOCK SMS OTP] Sent to: {}, OTP Code: {}", phone, otpCode);
    }

    public void sendPasswordResetEmail(String email, String resetTokenOrOtp) {
        logger.info("[NOTIFICATION - PASSWORD RESET EMAIL] Sent to: {}, Reset Token/OTP: {}", email, resetTokenOrOtp);
    }
}
