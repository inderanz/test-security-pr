package com.example.insecure;

import java.sql.*;
import java.util.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;

@Service
@RestController
public class InsecureService {
    
    // SECURITY ISSUE: Hardcoded database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/prod_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "password123";  // Should use environment variables
    
    // SECURITY ISSUE: Hardcoded API key
    private static final String STRIPE_API_KEY = "sk_test_1234567890abcdef";  // Should be in config
    
    // SECURITY ISSUE: Weak encryption key
    private static final String ENCRYPTION_KEY = "mysecretkey123";  // Should be 256-bit key
    
    // SECURITY ISSUE: SQL Injection vulnerability
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String cardNumber, 
                                @RequestParam String amount,
                                @RequestParam String userId) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // SECURITY ISSUE: SQL Injection - direct string concatenation
            String sql = "INSERT INTO payments (card_number, amount, user_id) VALUES ('" + 
                        cardNumber + "', " + amount + ", '" + userId + "')";
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
            // SECURITY ISSUE: Logging sensitive data
            System.out.println("Payment processed for card: " + cardNumber);
            
            return "Payment successful";
        } catch (SQLException e) {
            // SECURITY ISSUE: No proper error handling
            e.printStackTrace();
            return "Payment failed";
        }
    }
    
    // SECURITY ISSUE: Weak password hashing
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // Should use bcrypt/argon2
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return password;  // SECURITY ISSUE: Fallback to plain text
        }
    }
    
    // SECURITY ISSUE: Command injection vulnerability
    @PostMapping("/execute")
    public String executeCommand(@RequestParam String command) {
        try {
            // SECURITY ISSUE: Command injection
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            return output.toString();
        } catch (IOException e) {
            return "Error executing command";
        }
    }
    
    // SECURITY ISSUE: Path traversal vulnerability
    public void uploadFile(byte[] fileData, String fileName) {
        try {
            // SECURITY ISSUE: Path traversal vulnerability
            String uploadPath = "/uploads/" + fileName;  // Should validate filename
            
            // SECURITY ISSUE: No file type validation
            File file = new File(uploadPath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileData);
            fos.close();
            
            // SECURITY ISSUE: Logging file path
            System.out.println("File uploaded to: " + uploadPath);
            
        } catch (IOException e) {
            // SECURITY ISSUE: No proper error handling
            e.printStackTrace();
        }
    }
    
    // SECURITY ISSUE: Weak encryption implementation
    public String encrypt(String plaintext) {
        try {
            // SECURITY ISSUE: Using weak cipher
            Cipher cipher = Cipher.getInstance("DES");  // Should use AES-256
            
            // SECURITY ISSUE: Weak key generation
            SecretKey key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
            
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes());
            
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            // SECURITY ISSUE: No proper error handling
            return plaintext;  // Fallback to plain text
        }
    }
    
    // SECURITY ISSUE: Insecure deserialization
    @PostMapping("/data")
    public String processData(@RequestBody String data) {
        try {
            // SECURITY ISSUE: Insecure deserialization
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data.getBytes()));
            Object obj = ois.readObject();
            
            return "Processed: " + obj.toString();
        } catch (Exception e) {
            return "Error processing data";
        }
    }
    
    // SECURITY ISSUE: No input validation
    public boolean authenticateUser(String username, String password) {
        // SECURITY ISSUE: No input validation
        if (username == null || password == null) {
            return false;
        }
        
        // SECURITY ISSUE: No rate limiting check
        // SECURITY ISSUE: No account lockout
        
        // SECURITY ISSUE: Weak authentication logic
        return "admin".equals(username) && "password123".equals(password);
    }
} 