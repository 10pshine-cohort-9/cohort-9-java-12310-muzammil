package com._pearls.cms.util;

public final class PhoneUtils {

    private static final String PAK_MOBILE_REGEX = "^\\+923\\d{9}$";

    private PhoneUtils() {
    }

    public static String normalizePakistaniPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }

        String cleaned = phone
                .trim()
                .replaceAll("[\\s()-]", "");

        if (cleaned.startsWith("03")) {
            cleaned = "+92" + cleaned.substring(1);
        } else if (cleaned.startsWith("923")) {
            cleaned = "+" + cleaned;
        }

        if (!cleaned.matches(PAK_MOBILE_REGEX)) {
            throw new IllegalArgumentException("Invalid Pakistani mobile number.");
        }

        return cleaned;
    }
}