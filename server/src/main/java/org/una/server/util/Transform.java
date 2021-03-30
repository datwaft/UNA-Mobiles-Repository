package org.una.server.util;

public class Transform {
    public static String transformWeekday(Integer original) {
        return switch (original) {
            case 0 -> "Monday";
            case 1 -> "Tuesday";
            case 2 -> "Wednesday";
            case 3 -> "Thursday";
            case 4 -> "Friday";
            case 5 -> "Saturday";
            case 6 -> "Sunday";
            default -> "Invalid";
        };
    }
}
