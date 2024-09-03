package com.onedrinktoday.backend.global.security;

import com.onedrinktoday.backend.global.type.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtProvider {

  private final SecretKey secretKey;
  private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

  public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String createToken(String email, Role role) {
    return Jwts.builder()
        .subject(email)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
        .signWith(secretKey)
        .compact();
  }

  public String getEmail(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
        .getPayload().getSubject();
  }

  public boolean isTokenExpired(String refreshToken) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(refreshToken)
          .getPayload();

      Date expiration = claims.getExpiration();

      return expiration.before(new Date());
    } catch (RuntimeException e) {
      return true;
    }
  }
}
