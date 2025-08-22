package com.example.myapplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    // Hashes the password using SHA-256
    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hash the password
            byte[] hashedBytes = digest.digest(password.getBytes());

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Compares the entered password with the stored hashed password
    public static boolean checkPassword(String enteredPassword, String storedHashedPassword) {
        // Hash the entered password
        String hashedEnteredPassword = hashPassword(enteredPassword);

        // Compare the hashed entered password with the stored hashed password
        return hashedEnteredPassword != null && hashedEnteredPassword.equals(storedHashedPassword);
    }
}
