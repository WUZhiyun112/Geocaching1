//package com.example.backend.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//import java.security.Key;
//
//public class TokenService {
//
//    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
//
//    public String generateToken(String username) {
//        log.debug("Generating JWT token for username: {}", username);
//        String token = Jwts.builder()
//                .setSubject(username)
//                .signWith(SECRET_KEY)
//                .compact();
//        log.debug("Token generated: {}", token);
//        return token;
//    }
//
//
//}
package com.example.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.Key;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(SECRET_KEY)
                .compact();
    }
}
