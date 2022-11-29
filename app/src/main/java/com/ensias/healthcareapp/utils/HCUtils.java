package com.ensias.healthcareapp.utils;

public class HCUtils {
    public static boolean isValidEmail(String emailId) {
        String regexPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return emailId.matches(regexPattern);
    }
}
