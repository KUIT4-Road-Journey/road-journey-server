package com.road_journey.road_journey.utils;

import com.road_journey.road_journey.auth.config.JwtUtil;

public class TokenValidatorUtil {

    private TokenValidatorUtil() {
    }

    public static String validateToken(String token, JwtUtil jwtUtil) {
        if (!jwtUtil.validateToken(token)) {
            throw new SecurityException("Invalid or expired token");
        }

        return normalizeToken(token);
    }

    private static String normalizeToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
