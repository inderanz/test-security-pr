package com.example.secure;

import java.sql.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RestController
public class SecureService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Secure payment processing
    @PostMapping("/process-payment")
    public String processPayment(@RequestParam String cardNumber, 
                                @RequestParam String amount,
                                @RequestParam String userId) {
        try {
            // Input validation
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                return "Error: Invalid card number";
            }
            
            if (amount == null || !amount.matches("\\d+(\\.\\d{2})?")) {
                return "Error: Invalid amount";
            }
            
            // Use prepared statement to prevent SQL injection
            String sql = "INSERT INTO payments (card_number, amount, user_id) VALUES (?, ?, ?)";
            
            try (Connection conn = getSecureConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, cardNumber);
                stmt.setString(2, amount);
                stmt.setString(3, userId);
                
                stmt.executeUpdate();
                
                // Log without sensitive data
                System.out.println("Payment processed for user: " + userId);
                
                return "Payment successful";
            }
        } catch (SQLException e) {
            // Secure error handling
            System.err.println("Database error occurred");
            return "Payment failed";
        }
    }
    
    // Secure password hashing
    public String hashPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        return passwordEncoder.encode(password);
    }
    
    // Secure authentication
    public boolean authenticateUser(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        // Rate limiting would be implemented here
        // Account lockout would be implemented here
        
        try {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                return passwordEncoder.matches(password, user.getPasswordHash());
            }
        } catch (Exception e) {
            System.err.println("Authentication error");
        }
        
        return false;
    }
    
    private Connection getSecureConnection() throws SQLException {
        // Use environment variables for credentials
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        
        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new SQLException("Database configuration not found");
        }
        
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
} 