//package com.example.backend.controller;
//
//import com.example.backend.entity.User;
//import com.example.backend.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody User user) {
//        if (userService.findUserByUsername(user.getUsername()).isPresent()) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
//        if (userService.findUserByEmail(user.getEmail()).isPresent()) {
//            return ResponseEntity.badRequest().body("Email already exists");
//        }
//        userService.saveUser(user);
//        return ResponseEntity.ok("User registered successfully");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody User user) {
//        return userService.authenticateUser(user)
//                .map(jwt -> ResponseEntity.ok("Login successful. Token: " + jwt))
//                .orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
//    }
//}
//
package com.example.backend.controller;

import com.example.backend.dto.UserDto;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://10.0.2.2:8080")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private String secretKey = "5DS6EbEc493q9CS6sOR+z1Ok+O9nhUMGzo/7evUn7qo=";

    private static final Logger log = LoggerFactory.getLogger(UserController.class);


//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
//        if (userService.findUserByUsername(userDto.getUsername()).isPresent()) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
//        if (userService.findUserByEmail(userDto.getEmail()).isPresent()) {
//            return ResponseEntity.badRequest().body("Email already exists");
//        }
//        User newUser = new User();
//        newUser.setUsername(userDto.getUsername());
//        newUser.setEmail(userDto.getEmail());
//        newUser.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
//        userService.saveUser(newUser);
//        return ResponseEntity.ok("User registered successfully");
//    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        log.debug("register attempt for user: ");
        // 公共验证逻辑
        ResponseEntity<String> validationResponse = validateUserDetails(userDto);
        if (validationResponse != null) return validationResponse;

        User newUser = convertDtoToEntity(userDto);
        userService.saveUser(newUser);
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDto userDto) {
        log.debug("Login attempt for user: {}", userDto.getUsername());
        return userService.authenticateUser(userDto.getUsername(), userDto.getPassword())
                .map(validUser -> {
                    log.debug("Login successful for user: {}", userDto.getUsername());
                    return ResponseEntity.ok("Login successful. Token: " + generateJwtToken(validUser));
                })
                .orElseGet(() -> {
                    log.debug("Invalid credentials provided for user: {}", userDto.getUsername());
                    return ResponseEntity.status(401).body("Invalid credentials");
                });
    }


    private ResponseEntity<String> validateUserDetails(UserDto userDto) {
        if (userService.findUserByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userService.findUserByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        return null;
    }

    private User convertDtoToEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        return user;
    }


    // 生成 JWT 令牌
    private String generateJwtToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000);
        return Jwts.builder()
                .setSubject(user.getUsername())  // 正确：`user` 是 User 类型
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }



}
