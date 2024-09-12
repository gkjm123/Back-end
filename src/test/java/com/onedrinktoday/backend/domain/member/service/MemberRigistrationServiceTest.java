package com.onedrinktoday.backend.domain.member.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;
import static com.onedrinktoday.backend.global.exception.ErrorCode.EMAIL_EXIST;
import static com.onedrinktoday.backend.global.exception.ErrorCode.LOGIN_FAIL;
import static com.onedrinktoday.backend.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.onedrinktoday.backend.global.exception.ErrorCode.TOKEN_NOT_MATCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.onedrinktoday.backend.global.type.DrinkType;
import com.onedrinktoday.backend.global.type.Role;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberRigistrationServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Mock
  private RegionRepository regionRepository;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private EmailService emailService;

  private Member member;
  private Region region;
  private SignUp signUpRequest;
  private SignIn signInRequest;
  private UpdateInfo updateInfo;
  private List<DrinkType> favorDrinkTypes;

  @BeforeEach
  void setUp() {
    member = new Member();
    member.setEmail("john@google.com");
    member.setPassword("Password123!");
    member.setRefreshToken("refreshToken");

    region = new Region();
    region.setId(1L);
    region.setPlaceName("서울특별시");

    // favorDrinks 리스트 초기화
    favorDrinkTypes = Arrays.asList(DrinkType.SOJU, DrinkType.BEER);

    signUpRequest = new SignUp("John", "john@google.com", "Password123!", new Date(),
        favorDrinkTypes, true);
    signInRequest = new SignIn("john@google.com", "Password123!");
    updateInfo = new UpdateInfo("John", favorDrinkTypes, true);
  }

  @Test
  @DisplayName("회원 가입 성공")
  void successSignUp() {
    //given
    when(regionRepository.findById(anyLong())).thenReturn(Optional.of(region));

    member = Member.builder()
        .id(1L)
        .region(region)
        .name(signUpRequest.getName())
        .email(signUpRequest.getEmail())
        .birthDate(signUpRequest.getBirthDate())
        .favorDrinkType(signUpRequest.getFavorDrinkType())
        .role(Role.USER)
        .alarmEnabled(signUpRequest.isAlarmEnabled())
        .build();

    when(memberRepository.save(any(Member.class))).thenReturn(member);

    //when
    MemberResponse response = memberService.signUp(signUpRequest);

    //then
    assertEquals(member.getId(), response.getId());
    assertEquals(member.getRegion().getPlaceName(), response.getPlaceName());
    assertEquals(member.getName(), response.getName());
    assertEquals(member.getEmail(), response.getEmail());
    assertEquals(member.getBirthDate(), response.getBirthDate());
    assertEquals(member.getFavorDrinkType(), response.getFavorDrinkType());
    assertEquals(member.getRole(), response.getRole());
    assertEquals(member.isAlarmEnabled(), response.isAlarmEnabled());
  }

  @Test
  @DisplayName("회원 가입 실패 - 이메일 이미 존재")
  void failSignUp() {
    //given
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.signUp(signUpRequest));

    //then
    assertEquals(EMAIL_EXIST.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("로그인 성공")
  void successSignIn() {
    //given
    String email = member.getEmail();
    String password = member.getPassword();
    String encodedPassword = bCryptPasswordEncoder.encode(member.getEmail());

    Member member = Member.builder()
        .email(email)
        .password(encodedPassword)
        .role(Role.USER)
        .build();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(bCryptPasswordEncoder.matches(password, encodedPassword)).thenReturn(true);
    when(jwtProvider.createAccessToken(member.getId(),email, Role.USER)).thenReturn("accessToken");
    when(jwtProvider.createRefreshToken(member.getId(),email, Role.USER)).thenReturn("refreshToken");

    //when
    TokenDto tokenDto = memberService.signIn(signInRequest);

    //then
    assertEquals("accessToken", tokenDto.getAccessToken());
    assertEquals("refreshToken", tokenDto.getRefreshToken());
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 비밀번호")
  void failSignIn() {
    //given
    String email = member.getEmail();
    String wrongPassword = "wrongPassword";
    String encodedPassword = bCryptPasswordEncoder.encode(member.getPassword());

    Member member = Member.builder()
        .email(email)
        .password(encodedPassword)
        .role(Role.USER)
        .build();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(bCryptPasswordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.signIn(new SignIn(email, wrongPassword)));

    //then
    assertEquals(LOGIN_FAIL.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("리프레시 토큰 갱신 성공")
  void successRefreshAccessToken() {
    //given
    String email = member.getEmail();
    String refreshToken = member.getRefreshToken();
    String newAccessToken = "newAccessToken";

    when(jwtProvider.getEmail(refreshToken)).thenReturn(email);

    Member member = Member.builder()
        .email(email)
        .role(Role.USER)
        .refreshToken(refreshToken).build();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(jwtProvider.createAccessToken(member.getId(),email, Role.USER)).thenReturn(newAccessToken);

    //when
    TokenDto tokenDto = memberService.refreshAccessToken(refreshToken);

    //then
    assertEquals(newAccessToken, tokenDto.getAccessToken());
    assertEquals(refreshToken, tokenDto.getRefreshToken());
  }

  @Test
  @DisplayName("리프레시 토큰 갱신 실패 - 토큰 불일치")
  void failRefreshAccessToken() {
    //given
    String email = member.getEmail();
    String refreshToken = member.getRefreshToken();
    String wrongRefreshToken = "wrongRefreshToken";

    Member member = Member.builder()
        .email(email)
        .refreshToken(refreshToken)
        .build();

    when(jwtProvider.getEmail(wrongRefreshToken)).thenReturn(email);
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.refreshAccessToken(wrongRefreshToken));

    //then
    assertEquals(TOKEN_NOT_MATCH.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("회원 정보 조회 성공")
  void successGetMemberInfo() {
    //given
    String email = member.getEmail();
    String name = member.getName();
    Date birthDate = member.getBirthDate();

    Member member = Member.builder()
        .region(region)
        .email(email)
        .name(name)
        .role(Role.USER)
        .birthDate(birthDate)
        .alarmEnabled(true)
        .favorDrinkType(favorDrinkTypes)
        .deletedAt(null)
        .build();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

    //MemberDetail 사용하여 인증 정보 확인
    MemberDetail memberDetail = new MemberDetail(MemberResponse.from(member));
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(memberDetail, null));

    //when
    MemberResponse response = memberService.getMemberInfo();

    //then
    assertEquals(region.getPlaceName(), response.getPlaceName());
    assertEquals(email, response.getEmail());
    assertEquals(name, response.getName());
    assertEquals(birthDate, response.getBirthDate());
    assertEquals(Role.USER, response.getRole());
    assertEquals(favorDrinkTypes, response.getFavorDrinkType());
    assertTrue(response.isAlarmEnabled());
    assertNull(response.getDeletedAt());
  }

  @Test
  @DisplayName("회원 정보 조회 실패 - 사용자 정보 없음")
  void failGetMemberInfo() {
    //given
    String email = member.getEmail();
    member.setRegion(region);

    //MemberDetail 사용하여 인증 정보 확인
    MemberDetail memberDetail = new MemberDetail(MemberResponse.from(member));
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(memberDetail, null));

    //when
    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    //then
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.getMemberInfo());

    assertEquals(MEMBER_NOT_FOUND.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("회원 정보 업데이트 성공")
  void successUpdateMemberInfo() {
    //given
    Member existMember = Member.builder()
        .id(member.getId())
        .region(region)
        .name(member.getName())
        .email(member.getEmail())
        .birthDate(member.getBirthDate())
        .favorDrinkType(member.getFavorDrinkType())
        .role(member.getRole())
        .alarmEnabled(true)
        .imageUrl(member.getImageUrl())
        .build();

    Member updatedMember = Member.builder()
        .name(updateInfo.getName())
        .favorDrinkType(updateInfo.getFavorDrinkType())
        .alarmEnabled(updateInfo.isAlarmEnabled())
        .build();

    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existMember));
    when(regionRepository.findById(anyLong())).thenReturn(Optional.of(region));
    when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

    //MemberDetail 사용하여 인증 정보 확인
    MemberDetail memberDetail = new MemberDetail(MemberResponse.from(existMember));
    Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetail, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    //when
    MemberResponse response = memberService.updateMemberInfo(updateInfo);

    //then
    assertEquals(updatedMember.getRegion().getPlaceName(), response.getPlaceName());
    assertEquals(updatedMember.getName(), response.getName());
    assertEquals(updatedMember.getFavorDrinkType(), response.getFavorDrinkType());
    assertEquals(updatedMember.isAlarmEnabled(), response.isAlarmEnabled());
    assertEquals(updatedMember.getImageUrl(), response.getImageUrl());
  }

  @Test
  @DisplayName("회원 정보 업데이트 실패 - 잘못된 지역 ID")
  void failUpdateMemberInfo() {
    //given
    Member existMember = Member.builder()
        .id(member.getId())
        .region(region)
        .name(member.getName())
        .email(member.getEmail())
        .birthDate(member.getBirthDate())
        .favorDrinkType(member.getFavorDrinkType())
        .role(member.getRole())
        .alarmEnabled(true)
        .imageUrl(member.getImageUrl())
        .build();

    UpdateInfo wrongUpdateInfo = new UpdateInfo(updateInfo.getName(),
        updateInfo.getFavorDrinkType(), updateInfo.isAlarmEnabled());

    //MemberDetail 사용하여 인증 정보 확인
    MemberDetail memberDetail = new MemberDetail(MemberResponse.from(existMember));
    Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetail, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existMember));
    when(regionRepository.findById(anyLong())).thenReturn(Optional.empty());

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.updateMemberInfo(wrongUpdateInfo));

    //then
    assertEquals(REGION_NOT_FOUND.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("비밀번호 재설정 요청 성공")
  void successRequestPasswordReset() {
    //given
    String email = member.getEmail();
    String token = "token";
    String resetLink = "http://localhost:8080/api/members/reset-password?token=" + token;

    Member existMember = new Member();
    existMember.setEmail(email);
    existMember.setRole(Role.USER);

    when(memberRepository.findByEmail(existMember.getEmail())).thenReturn(Optional.of(existMember));
    when(jwtProvider.createResetToken(member.getId(),email, Role.USER)).thenReturn(token);

    ArgumentCaptor<String> emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> resetLinkArgumentCaptor = ArgumentCaptor.forClass(String.class);

    //when
    memberService.requestPasswordReset(email);

    //then
    verify(emailService, times(1)).sendPasswordResetEmail(emailArgumentCaptor.capture(),
        resetLinkArgumentCaptor.capture());
    assertEquals(email, emailArgumentCaptor.getValue());
    assertEquals(resetLink, resetLinkArgumentCaptor.getValue());
  }

  @Test
  @DisplayName("비밀번호 재설정 요청 실패 - 이메일 없음")
  void failRequestPasswordReset() {
    //given
    String email = member.getEmail();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.requestPasswordReset(email));

    //then
    assertEquals(EMAIL_NOT_FOUND.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("비밀번호 재설정 성공")
  void successResetPassword() {
    //given
    String token = "token";
    String newPassword = "newPassword";
    String email = member.getEmail();
    String encodedPassword = bCryptPasswordEncoder.encode(member.getPassword());

    Member member = new Member();
    member.setEmail(email);
    member.setPassword(member.getPassword());

    when(jwtProvider.getEmail(token)).thenReturn(email);
    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedPassword);

    //when
    memberService.resetPassword(token, newPassword);

    //then
    assertEquals(encodedPassword, member.getPassword());
  }

  @Test
  @DisplayName("비밀번호 재설정 실패 - 이메일 없음")
  void failResetPassword() {
    //given
    String token = "token";
    String newPassword = "newPassword";
    String email = member.getEmail();

    when(jwtProvider.getEmail(token)).thenReturn(email);
    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.resetPassword(token, newPassword));

    //then
    assertEquals(ErrorCode.EMAIL_NOT_FOUND.getMessage(), customException.getMessage());
  }

  @Test
  @DisplayName("비밀번호 변경 성공")
  void successChangePassword() {
    //given
    String email = member.getEmail();
    String password = member.getPassword();
    String newPassword = "newPassword";
    String encodedPassword = bCryptPasswordEncoder.encode(member.getPassword());

    Member member = new Member();
    member.setEmail(email);
    member.setPassword(bCryptPasswordEncoder.encode(password));

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedPassword);
    when(bCryptPasswordEncoder.matches(password, member.getPassword())).thenReturn(true);
    when(bCryptPasswordEncoder.matches(newPassword, member.getPassword())).thenReturn(false);

    //when
    memberService.changePassword(email, password, newPassword);

    //then
    assertEquals(encodedPassword, member.getPassword());
  }

  @Test
  @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 틀림")
  void failChangePassword() {
    //given
    String email = member.getEmail();
    String password = "wrongPassword";
    String newPassword = "newPassword";
    String encodedPassword = bCryptPasswordEncoder.encode(member.getPassword());

    Member member = new Member();
    member.setEmail(email);
    member.setPassword(bCryptPasswordEncoder.encode(encodedPassword));

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
    when(bCryptPasswordEncoder.matches(password, member.getPassword())).thenReturn(false);

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.changePassword(email, password, newPassword));

    //then
    assertEquals(ErrorCode.LOGIN_FAIL.getMessage(), customException.getMessage());
  }
}