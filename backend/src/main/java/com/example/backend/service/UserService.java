package com.example.backend.service;

import com.example.backend.dto.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public UserService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public Optional<User> findUserByUsername(String username) {
        log.debug("enter findbyusername");
        log.debug("Searching for user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresentOrElse(
                u -> log.debug("User found: {}", u.getUsername()),
                () -> log.debug("No user found with username: {}", username)
        );
        return user;
    }

    public Optional<User> findUserByEmail(String email) {
        log.debug("Searching for user by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        user.ifPresentOrElse(
                u -> log.debug("User found: {}", u.getEmail()),
                () -> log.debug("No user found with email: {}", email)
        );
        return user;
    }

    public void saveUser(User user) {
        log.debug("Saving user: {}", user.getUsername());
        // 不再进行加密
        userRepository.save(user);
        log.debug("User saved successfully: {}", user.getUsername());
    }

//    public Optional<User> authenticateUser(String username, String password) {
//        log.debug("Attempting to authenticate user: {}", username);
//        return userRepository.findByUsername(username)
//                .filter(user -> {
//                    boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
//                    log.debug("Password match result for {}: {}", username, passwordMatches);
//                    return passwordMatches;
//                });
//    }

    public UserResponse authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return new UserResponse(false, "Username does not exist", null, null, null);
        }
        User user = userOpt.get();
        boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
        if (!passwordMatches) {
            return new UserResponse(false, "Invalid credentials", null, null, null);
        }
        String token = tokenService.generateToken(user.getUsername());
        return new UserResponse(true, "Login successful.", token, user.getUsername(), user.getEmail());
    }


//    public ResponseEntity<Map<String, Object>> authenticateUser(String username, String password) {
//        Optional<User> userOpt = userRepository.findByUsername(username);
//        if (!userOpt.isPresent()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Username does not exist"));
//        }
//
//        User user = userOpt.get();
//        boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
//        if (!passwordMatches) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
//        }
//
//        String token = generateToken(user.getUsername());
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", token);
//        response.put("username", user.getUsername());
//        response.put("email", user.getEmail());
//        return ResponseEntity.ok(response);
//    }



//    public String generateToken(String username) {
//        log.debug("Generating JWT token for username: {}", username);
//        String token = Jwts.builder()
//                .setSubject(username)
//                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
//                .compact();
//        log.debug("Token generated: {}", token);
//        return token;
//    }


}
