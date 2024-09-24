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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedrinktoday.backend.domain.comment.entity.Comment;
import com.onedrinktoday.backend.domain.comment.repository.CommentRepository;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignIn;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignUp;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.UpdateInfo;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.domain.tagFollow.repository.TagFollowRepository;
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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class MemberRegistrationServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Mock
  private PostRepository postRepository;

  @Mock
  private RegistrationRepository registrationRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private TagFollowRepository tagFollowRepository;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private EmailService emailService;

  private Member member;
  private Region region;
  private SignUp signUpRequest;
  private SignIn signInRequest;
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

    MemberDetail memberDetail = new MemberDetail(MemberResponse.from(member));
    Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetail, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("회원 가입 성공")
  void successSignUp() {
    // given
    when(memberRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());

    Member expectedMember = Member.builder()
        .id(1L)
        .region(region)
        .name(signUpRequest.getName())
        .email(signUpRequest.getEmail())
        .birthDate(signUpRequest.getBirthDate())
        .favorDrinkType(signUpRequest.getFavorDrinkType())
        .role(Role.USER)
        .alarmEnabled(signUpRequest.isAlarmEnabled())
        .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
        .build();

    when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

    //when
    MemberResponse response = memberService.signUp(signUpRequest);

    //then
    assertEquals(expectedMember.getId(), response.getId());
    assertEquals(expectedMember.getRegion().getPlaceName(), response.getPlaceName());
    assertEquals(expectedMember.getName(), response.getName());
    assertEquals(expectedMember.getEmail(), response.getEmail());
    assertEquals(expectedMember.getBirthDate(), response.getBirthDate());
    assertEquals(expectedMember.getFavorDrinkType(), response.getFavorDrinkType());
    assertEquals(expectedMember.getRole(), response.getRole());
    assertEquals(expectedMember.isAlarmEnabled(), response.isAlarmEnabled());
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
    when(jwtProvider.createAccessToken(member.getId(), email, Role.USER)).thenReturn("accessToken");
    when(jwtProvider.createRefreshToken(member.getId(), email, Role.USER)).thenReturn(
        "refreshToken");

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
    when(jwtProvider.createAccessToken(member.getId(), email, Role.USER)).thenReturn(
        newAccessToken);

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

    UpdateInfo updateInfo = UpdateInfo.builder()
        .name("변경 이름")
        .favorDrinkType(List.of(DrinkType.BEER, DrinkType.WINE))
        .alarmEnabled(false)
        .build();

    Member updatedMember = Member.builder()
        .id(existMember.getId())
        .name(updateInfo.getName())
        .favorDrinkType(updateInfo.getFavorDrinkType())
        .alarmEnabled(existMember.isAlarmEnabled())
        .imageUrl(existMember.getImageUrl())
        .build();

    when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(existMember));
    when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

    //when
    MemberResponse response = memberService.updateMemberInfo(updateInfo);

    //then
    assertEquals(updatedMember.getName(), response.getName());
    assertEquals(updatedMember.getFavorDrinkType(), response.getFavorDrinkType());
    assertEquals(updatedMember.isAlarmEnabled(), response.isAlarmEnabled());
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
    when(jwtProvider.createResetToken(member.getId(), email, Role.USER)).thenReturn(token);

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

  @Test
  @Transactional
  void successWithdrawMember() {
    //given
    Member existMember = Member.builder()
        .id(1L)
        .region(region)
        .name(member.getName())
        .email(member.getEmail())
        .birthDate(member.getBirthDate())
        .favorDrinkType(member.getFavorDrinkType())
        .role(member.getRole())
        .alarmEnabled(true)
        .imageUrl(member.getImageUrl())
        .build();

    Post post = Post.builder()
        .id(1L)
        .member(existMember)
        .build();
    Comment comment = Comment.builder()
        .id(1L)
        .member(existMember)
        .build();
    Registration registration = Registration.builder()
        .id(1L)
        .member(existMember)
        .build();

    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existMember));
    when(postRepository.findAllByMember(existMember)).thenReturn(List.of(post));
    when(commentRepository.findAllByMember(existMember)).thenReturn(List.of(comment));
    when(registrationRepository.findAllByMember(existMember)).thenReturn(List.of(registration));

    //when
    memberService.withdrawMember();

    //then
    verify(tagFollowRepository, times(1)).deleteByMember(existMember);
    verify(postRepository, times(1)).findAllByMember(existMember);
    verify(commentRepository, times(1)).findAllByMember(existMember);
    verify(registrationRepository, times(1)).findAllByMember(existMember);
    verify(postRepository, times(1)).saveAll(List.of(post));
    verify(commentRepository, times(1)).saveAll(List.of(comment));
    verify(registrationRepository, times(1)).saveAll(List.of(registration));
    verify(memberRepository, times(1)).delete(existMember);
  }

  @Test
  @DisplayName("회원 탈퇴 실패 - 사용자 정보 없음")
  void failWithdrawMember() {
    //given
    String email = member.getEmail();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    //when
    CustomException customException = assertThrows(CustomException.class,
        () -> memberService.withdrawMember());

    //then
    assertEquals(MEMBER_NOT_FOUND.getMessage(), customException.getMessage());
  }
}