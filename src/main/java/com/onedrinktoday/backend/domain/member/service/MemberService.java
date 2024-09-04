package com.onedrinktoday.backend.domain.member.service;

import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignIn;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignUp;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.UpdateInfo;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import com.onedrinktoday.backend.global.security.JwtProvider;
import com.onedrinktoday.backend.global.security.MemberDetail;
import com.onedrinktoday.backend.global.security.TokenDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
  public TokenDto signIn(SignIn request) {
    Member member = memberRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAIL));

    if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_FAIL);
    }

    String accessToken = jwtProvider.createAccessToken(member.getEmail(), member.getRole());
    String refreshToken = jwtProvider.createRefreshToken(member.getEmail(), member.getRole());

    return TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional(readOnly = true)
  public TokenDto refreshAccessToken(String refreshToken) {

    String email;

    try {
      //유효기간 만료시 exception 발생
      email = jwtProvider.getEmail(refreshToken);

    } catch (SignatureException | UnsupportedJwtException | ExpiredJwtException
             | MalformedJwtException e) {
      throw new CustomException(ErrorCode.TOKEN_EXPIRED);
    }

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    String AccessToken = jwtProvider.createAccessToken(member.getEmail(), member.getRole());

    return TokenDto.builder()
        .accessToken(AccessToken)
        .build();

  }

  @Transactional(readOnly = true)
  public MemberResponse getMemberInfo(Long memberId) {

    Member member = getMember();

    if (member.getId().equals(memberId)) {
      return MemberResponse.from(member);
    } else {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }
  }

  @Transactional
  public MemberResponse updateMemberInfo(Long memberId, UpdateInfo updateInfo) {

    Member member = getMember();

    if (!member.getId().equals(memberId)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    updateMemberFields(member, updateInfo);

    return MemberResponse.from(memberRepository.save(member));
  }

  //멤버 정보 필요시 MemberService 주입받아 메서드 사용
  @Transactional(readOnly = true)
  public Member getMember() {

    MemberDetail memberDetail =
        (MemberDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return memberRepository.findByEmail(memberDetail.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  private void updateMemberFields(Member member, UpdateInfo updateInfo) {
    if (updateInfo.getRegionId() != null) {
      member.setRegion(regionRepository.findById(updateInfo.getRegionId())
          .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND)));
    }
    if (updateInfo.getName() != null) {
      member.setName(updateInfo.getName());
    }
    if (updateInfo.getFavorDrink() != null) {
      member.setFavorDrink(updateInfo.getFavorDrink());
    }
    member.setAlarmEnabled(updateInfo.isAlarmEnabled());
    if (updateInfo.getImageUrl() != null) {
      member.setImageUrl(updateInfo.getImageUrl());
    }
  }
}
