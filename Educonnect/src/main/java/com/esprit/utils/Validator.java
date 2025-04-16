package com.esprit.utils;

public class Validator {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidTextLength(String text, int min, int max) {
        return text != null && text.length() >= min && text.length() <= max;
    }

    public static boolean isEightDigitNumber(String text) {
        return text != null && text.matches("\\d{8}");
    }

    public static boolean isValidSalary(String salary) {
        return salary != null && salary.matches("\\d+(\\.\\d+)?");
    }
}
