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
  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30; // 엑세스 토큰 기한
  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 리프레시 토큰 기한
  private static final long PASSWORD_RESET_EXPIRE_TIME = 1000L * 60 * 30; // 비밀번호 재설정 토큰 기한


  public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String createAccessToken(Long memberId, String email, Role role) {
    return Jwts.builder()
        .subject(email)
        .claim("member_id", memberId)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
        .signWith(secretKey)
        .compact();
  }

  public String createRefreshToken(Long memberId, String email, Role role) {
    return Jwts.builder()
        .subject(email)
        .claim("member_id", memberId)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
        .signWith(secretKey)
        .compact();
  }

  // 비밀번호 재설정 토큰 생성
  public String createResetToken(Long memberId, String email, Role role) {
    return Jwts.builder()
        .subject(email)
        .claim("member_id", memberId)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_EXPIRE_TIME))
        .signWith(secretKey)
        .compact();
  }

  public String getEmail(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getSubject();
  }

  public Long getMemberId(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    return claims.get("member_id", Long.class);  // member_id 추출
  }
}
