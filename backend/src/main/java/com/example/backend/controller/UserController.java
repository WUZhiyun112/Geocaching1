
//
package com.example.backend.controller;

import com.example.backend.dto.UserDto;
import com.example.backend.dto.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import com.example.backend.service.TokenService;
import com.example.backend.repository.UserRepository;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

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

    private TokenService tokenService;
    private UserRepository userRepository;
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


//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
//        log.debug("Login attempt for user: {}", userDto.getUsername());
//        return userService.authenticateUser(userDto.getUsername(), userDto.getPassword());
//    }
@PostMapping("/login")
public ResponseEntity<UserResponse> loginUser(@RequestBody UserDto userDto) {
    UserResponse response = userService.authenticateUser(userDto.getUsername(), userDto.getPassword());
    if (!response.isSuccess()) {
        return ResponseEntity.status(401).body(response);
    }
    return ResponseEntity.ok(response);
}
    @PostMapping("/users/verify")
    public ResponseEntity<UserResponse> verifyUserCredentials(@RequestBody UserDto userDto) {
        Optional<User> userOpt = userRepository.findByUsernameAndEmail(userDto.getUsername(), userDto.getEmail());
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(new UserResponse(true, "Credentials valid", null, userDto.getUsername(), userDto.getEmail()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponse(false, "Invalid credentials", null, null, null));
        }
    }

    @GetMapping("/api/users/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String token) {
        String username = tokenService.getUsernameFromToken(token.substring(7)); // Assuming token prefix "Bearer "
        if (username == null) {
            return ResponseEntity.status(401).body(new UserResponse(false, "Invalid token", null, null, null));
        }

        User user = userService.findUserByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(new UserResponse(false, "User not found", null, null, null));
        }

        return ResponseEntity.ok(new UserResponse(true, "User details fetched successfully", null, user.getUsername(), user.getEmail()));
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
