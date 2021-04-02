package org.una.server.util;

import java.time.ZonedDateTime;
import java.util.UUID;

public class TokenUtils {
    public static String generateToken() {
        return UUID.randomUUID().toString().toUpperCase() + ZonedDateTime.now().toInstant().toEpochMilli();
    }
}
