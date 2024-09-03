package com.onedrinktoday.backend.domain.member.service;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignIn;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignUp;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import com.onedrinktoday.backend.global.security.JwtProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final RegionRepository regionRepository;
  private final JwtProvider jwtProvider;

  @Transactional
  public MemberResponse signUp(SignUp request) {

    if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_EXIST);
    }

    Member member = Member.from(request);

    Region region = regionRepository.findById(request.getRegionId())
        .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));

    member.setRegion(region);
    member.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

    return MemberResponse.from(memberRepository.save(member));
  }

  @Transactional(readOnly = true)
  public String signIn(SignIn request) {

    Member member = memberRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAIL));

    if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_FAIL);
    }

    return jwtProvider.createToken(member.getEmail(), member.getRole());
  }

  @Transactional(readOnly = true)
  public String refreshAccessToken(String refreshToken) {
    String email;
    try {
      email = jwtProvider.getEmail(refreshToken);

      if (jwtProvider.isTokenExpired(refreshToken)) {
        throw new CustomException(ErrorCode.TOKEN_EXPIRED);
      }

    } catch (JwtException e) {
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAIL));

    return jwtProvider.createToken(member.getEmail(), member.getRole());
  }
}
