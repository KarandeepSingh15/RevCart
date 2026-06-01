package com.revcart.common_security.service;
import com.revcart.common_security.config.JwtConstants;
import com.revcart.common_security.config.JwtProperties;
import com.revcart.common_security.dto.JwtUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(JwtUserInfo user) {

        return Jwts.builder()
                .subject(user.email())
                .claim(JwtConstants.USER_ID, user.userId())
                .claim(JwtConstants.ROLE, user.role())
                .claim(JwtConstants.VERSION,"1")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtProperties.expiration()))
                .signWith(Keys.hmacShaKeyFor(
                        jwtProperties.secret().getBytes()))
                .compact();
    }
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes()
        );
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {

        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
