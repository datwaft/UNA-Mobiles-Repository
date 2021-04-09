package org.una.server.util;

public class TransformUtils {
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

    public static Integer transformWeekday(String original) {
        return switch (original) {
            case "Monday" -> 0;
            case "Tuesday" -> 1;
            case "Wednesday" -> 2;
            case "Thursday" -> 3;
            case "Friday" -> 4;
            case "Saturday" -> 5;
            case "Sunday" -> 6;
            default -> -1;
        };
    }
}
