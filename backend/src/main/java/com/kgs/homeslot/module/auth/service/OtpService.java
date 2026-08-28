package com.kgs.homeslot.module.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Value("${app.otp.ttl-seconds:300}")
    private int otpTtlSeconds;

    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;

    // Fallback in-memory cache if Redis is offline during local dev
    private final Map<String, OtpData> inMemoryOtpStore = new ConcurrentHashMap<>();

    private static class OtpData {
        String code;
        long expiryTime;
        int attempts;

        OtpData(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
            this.attempts = 0;
        }
    }

    public String generateOtp(String key) {
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        long expiry = System.currentTimeMillis() + (otpTtlSeconds * 1000L);
        inMemoryOtpStore.put(key, new OtpData(otp, expiry));

        logger.info("[OTP GENERATED] Key: {}, OTP: {} (Expires in {}s)", key, otp, otpTtlSeconds);
        return otp;
    }

    public boolean verifyOtp(String key, String inputOtp) {
        OtpData data = inMemoryOtpStore.get(key);
        if (data == null) {
            logger.warn("[OTP VERIFY FAILED] No OTP found for key: {}", key);
            return false;
        }

        if (System.currentTimeMillis() > data.expiryTime) {
            inMemoryOtpStore.remove(key);
            logger.warn("[OTP VERIFY FAILED] OTP expired for key: {}", key);
            return false;
        }

        if (data.attempts >= maxAttempts) {
            inMemoryOtpStore.remove(key);
            logger.warn("[OTP VERIFY FAILED] Max attempts reached for key: {}", key);
            return false;
        }

        data.attempts++;

        if (data.code.equals(inputOtp)) {
            inMemoryOtpStore.remove(key);
            logger.info("[OTP VERIFIED SUCCESS] Key: {}", key);
            return true;
        }

        return false;
    }
}
