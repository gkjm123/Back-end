package com.onedrinktoday.backend.global.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final MemberDetailService memberDetailService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = request.getHeader("Access-Token");

    //헤더에 토큰이 없으면 Auth 저장 없이 필터 통과
    if (StringUtils.hasText(token)) {

      try {
        //getEmail 실행시 유효성 체크 후 email 정보 반환
        MemberDetail memberDetail = memberDetailService.loadUserByUsername(
            jwtProvider.getEmail(token));

        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                memberDetail, null, memberDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (SignatureException | UnsupportedJwtException | ExpiredJwtException |
               MalformedJwtException e) {

        //만료된 토큰일 때 401번 응답 반환
        if (e instanceof ExpiredJwtException) {
          response.setStatus(401);
        }

        //이외 토큰 유효성 체크 실패시 500번 응답 반환
        else {
          response.setStatus(500);
        }
        response.flushBuffer();
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}