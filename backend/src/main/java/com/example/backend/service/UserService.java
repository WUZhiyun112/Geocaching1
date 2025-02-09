package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Value("${jwt.secret}")
    private String SECRET_KEY;

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

    public Optional<User> authenticateUser(String username, String password) {
        log.debug("Attempting to authenticate user: {}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
            log.debug("Password provided: {}", password);  // 显示提供的密码（注意安全风险，生产环境不应该这么做）
            log.debug("Password hash in database: {}", user.getPasswordHash());
            log.debug("Password match result for {}: {}", username, passwordMatches);
            return userOpt.filter(u -> passwordMatches);
        } else {
            log.debug("No user found with username: {}", username);
            return Optional.empty();
        }
    }


    public String generateToken(String username) {
        log.debug("Generating JWT token for username: {}", username);
        String token = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        log.debug("Token generated: {}", token);
        return token;
    }
}
