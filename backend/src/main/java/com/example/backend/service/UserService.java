package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
    }

    public Optional<User> authenticateUser(String username, String password) {
        // 查找用户，并验证密码
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }



    public String generateToken(String username) {
        // 使用密钥加密生成 JWT 令牌
        return Jwts.builder()
                .setSubject(username)  // 设置主题为用户名
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // 使用密钥加密
                .compact();
    }
}
