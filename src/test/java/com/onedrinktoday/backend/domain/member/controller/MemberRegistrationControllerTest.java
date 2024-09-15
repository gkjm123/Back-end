package com.onedrinktoday.backend.domain.member.controller;

import static com.onedrinktoday.backend.global.exception.ErrorCode.EMAIL_NOT_FOUND;
import static com.onedrinktoday.backend.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.onedrinktoday.backend.global.exception.ErrorCode.LOGIN_FAIL;
import static com.onedrinktoday.backend.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.domain.member.dto.ChangePasswordRequestDTO;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetDTO;
import com.onedrinktoday.backend.domain.member.dto.PasswordResetRequest;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.security.JwtProvider;
import com.onedrinktoday.backend.global.security.TokenDto;
import com.onedrinktoday.backend.global.type.DrinkType;
import com.onedrinktoday.backend.global.type.Role;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(MemberController.class)
public class MemberRegistrationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberService memberService;

  @MockBean
  private JwtProvider jwtProvider;

  private static final String VALID_EMAIL = "john.doe@examples.com";
  private static final String VALID_PASSWORD = "Password123!";
  private static final String NEW_PASSWORD = "newPassword123!";
  private static final String WRONG_PASSWORD = "wrongPassword!";
  private static final String INVALID_TOKEN = "invalidToken";
  private static final String REFRESH_TOKEN = "refreshToken";
  private static final String TOKEN = "token";
  private static final String WRONG_EMAIL = "wrong.email@examples.com";

  private MemberRequest.SignUp signUpRequest;
  private MemberResponse memberResponse;
  private MemberRequest.UpdateInfo updateInfo;
  private PasswordResetRequest passwordResetRequest;
  private ChangePasswordRequestDTO changePasswordRequestDTO;
  private TokenDto tokenDto;

  @BeforeEach
  public void setUp() {
    signUpRequest = MemberRequest.SignUp.builder()
        .name("JohnDoe")
        .email(VALID_EMAIL)
        .password(VALID_PASSWORD)
        .birthDate(new Date())
        .favorDrinkType(List.of(DrinkType.SOJU, DrinkType.DISTILLED_SPIRITS))
        .alarmEnabled(true)
        .build();

    memberResponse = MemberResponse.builder()
        .id(1L)
        .placeName("서울특별시")
        .name("JohnDoe")
        .email(VALID_EMAIL)
        .birthDate(new Date())
        .favorDrinkType(List.of(DrinkType.SOJU, DrinkType.DISTILLED_SPIRITS))
        .role(Role.USER)
        .alarmEnabled(true)
        .createdAt(LocalDateTime.now())
        .build();

    updateInfo = new MemberRequest.UpdateInfo(
        "JohnDoeBa", List.of(DrinkType.BEER), false);

    tokenDto = TokenDto.builder()
        .accessToken("accessToken")
        .refreshToken(REFRESH_TOKEN)
        .build();

    passwordResetRequest = new PasswordResetRequest(VALID_EMAIL);
    changePasswordRequestDTO = new ChangePasswordRequestDTO(VALID_PASSWORD, NEW_PASSWORD);
  }

  @Test
  @DisplayName("회원가입 성공")
  public void successSignUp() throws Exception {
    //then
    given(memberService.signUp(any(MemberRequest.SignUp.class))).willReturn(memberResponse);

    //when
    //then
    mockMvc.perform(post("/api/members/signup")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(signUpRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(memberResponse.getName()))
        .andExpect(jsonPath("$.email").value(memberResponse.getEmail()))
        .andExpect(jsonPath("$.favorDrinkType[0]").value(memberResponse.getFavorDrinkType().get(0).toString()))
        .andExpect(jsonPath("$.role").value(memberResponse.getRole().toString()))
        .andExpect(jsonPath("$.alarmEnabled").value(memberResponse.isAlarmEnabled()))
        .andDo(print());
  }

  @Test
  @DisplayName("회원가입 실패 - 회원 정보 미입력")
  public void failSignUp() throws Exception {
    //given
    MemberRequest.SignUp invalidRequest = MemberRequest.SignUp.builder()
        .name(null)
        .email(null)
        .password(null)
        .birthDate(null)
        .favorDrinkType(List.of(DrinkType.SOJU, DrinkType.DISTILLED_SPIRITS))
        .alarmEnabled(true)
        .build();

    //when
    //then
    mockMvc.perform(post("/api/members/signup")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("로그인 성공")
  public void successSignIn() throws Exception {
    //given
    MemberRequest.SignIn request = new MemberRequest.SignIn(VALID_EMAIL, VALID_PASSWORD);

    given(memberService.signIn(any(MemberRequest.SignIn.class))).willReturn(tokenDto);

    //when
    //then
    mockMvc.perform(post("/api/members/signin")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("로그인 실패 - 이메일 or 비밀번호 잘못된 입력")
  public void failSignIn() throws Exception {
    //given
    MemberRequest.SignIn request = new MemberRequest.SignIn(WRONG_EMAIL, WRONG_PASSWORD);

    given(memberService.signIn(any(MemberRequest.SignIn.class)))
        .willThrow(new CustomException(LOGIN_FAIL));

    //when
    //then
    mockMvc.perform(post("/api/members/signin")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(LOGIN_FAIL.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 재설정 요청 성공")
  public void successRequestPasswordReset() throws Exception {
    //when
    //then
    mockMvc.perform(post("/api/members/request-password-reset")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(passwordResetRequest)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 재설정 요청 실패 - 이메일 미등록")
  public void failRequestPasswordReset() throws Exception {
    //given
    String wrongEmail = WRONG_EMAIL;

    willThrow(new CustomException(EMAIL_NOT_FOUND)).given(memberService)
        .requestPasswordReset(wrongEmail);

    //when
    //then
    mockMvc.perform(post("/api/members/request-password-reset")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new PasswordResetRequest(wrongEmail))))
        .andExpect(status().isNotFound())
        .andExpect(content().string(EMAIL_NOT_FOUND.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 재설정 페이지 리디렉션 성공")
  public void successShowResetPasswordPage() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(get("/api/members/password-reset")
            .with(csrf())
            .param("token", TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("비밀번호 재설정 페이지. 토큰: " + TOKEN))
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 재설정 페이지 - 토큰 누락")
  public void failShowResetPasswordPage() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(get("/api/members/password-reset"))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 재설정 성공 (비밀번호 모를 경우)")
  public void successResetPassword() throws Exception {
    //given
    PasswordResetDTO passwordResetDTO = new PasswordResetDTO(TOKEN, NEW_PASSWORD);

    //when
    //then
    mockMvc.perform(post("/api/members/password-reset")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(passwordResetDTO)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 재설정 실패 (비밀번호 모를 경우) - 유효하지 않은 토큰 입력")
  public void failResetPassword() throws Exception {
    //given
    PasswordResetDTO passwordResetDTO = new PasswordResetDTO(INVALID_TOKEN, NEW_PASSWORD);

    willThrow(new CustomException(INVALID_REFRESH_TOKEN)).given(memberService)
        .resetPassword(passwordResetDTO.getToken(), passwordResetDTO.getNewPassword());

    //when
    //then
    mockMvc.perform(post("/api/members/password-reset")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(passwordResetDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(INVALID_REFRESH_TOKEN.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 변경 성공 (비밀번호 알고 있을 경우)")
  public void successChangePassword() throws Exception {
    //given
    given(jwtProvider.getEmail(TOKEN)).willReturn(VALID_EMAIL);

    //when
    //then
    mockMvc.perform(post("/api/members/password-change")
            .with(csrf())
            .header("Access-Token", TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(changePasswordRequestDTO)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("비밀번호 변경 실패 (비밀번호 알고 있을 경우) - 현재 비밀번호 틀림")
  public void failChangePassword() throws Exception {
    //given
    ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(WRONG_PASSWORD,
        NEW_PASSWORD);

    given(jwtProvider.getEmail(TOKEN)).willReturn(VALID_EMAIL);
    willThrow(new CustomException(LOGIN_FAIL)).given(memberService)
        .changePassword(VALID_EMAIL, request.getCurrentPassword(), request.getNewPassword());

    //when
    //then
    mockMvc.perform(post("/api/members/password-change")
            .with(csrf())
            .header("Access-Token", TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isBadRequest()) // 400 Bad Request
        .andExpect(content().string(LOGIN_FAIL.getMessage())) // 예외 메시지 검증
        .andDo(print());
  }

  @Test
  @DisplayName("리프레시 토큰 갱신 성공")
  public void successRefreshAccessToken() throws Exception {
    //given
    given(memberService.refreshAccessToken(REFRESH_TOKEN)).willReturn(tokenDto);

    //when
    //then
    mockMvc.perform(post("/api/members/refresh")
            .with(csrf())
            .header("Refresh-Token", REFRESH_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(tokenDto.getAccessToken()))
        .andExpect(jsonPath("$.refreshToken").value(tokenDto.getRefreshToken()))
        .andDo(print());
  }

  @Test
  @DisplayName("리프레시 토큰 갱신 실패 - 토큰이 유효하지 않음")
  public void failRefreshAccessToken() throws Exception {
    //given
    given(memberService.refreshAccessToken(INVALID_TOKEN))
        .willThrow(new CustomException(INVALID_REFRESH_TOKEN));

    //when
    //then
    mockMvc.perform(post("/api/members/refresh")
            .with(csrf())
            .header("Refresh-Token", INVALID_TOKEN))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(INVALID_REFRESH_TOKEN.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("회원 정보 조회 성공")
  public void successGetMemberInfo() throws Exception {
    //given
    given(memberService.getMemberInfo()).willReturn(memberResponse);

    //when
    //then
    mockMvc.perform(get("/api/members")
            .with(csrf()))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("회원 정보 조회 실패 - 회원 정보가 없음")
  public void failGetMemberInfo() throws Exception {
    //given
    given(memberService.getMemberInfo())
        .willThrow(new CustomException(MEMBER_NOT_FOUND));

    //when
    //then
    mockMvc.perform(get("/api/members")
            .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(MEMBER_NOT_FOUND.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("회원 정보 수정 성공")
  public void successUpdateMemberInfo() throws Exception {
    //given
    MemberResponse updatedMemberResponse = MemberResponse.builder()
        .id(1L)
        .placeName("서울특별시")
        .name(updateInfo.getName())
        .email(VALID_EMAIL)
        .birthDate(new Date())
        .favorDrinkType(updateInfo.getFavorDrinkType())
        .role(Role.USER)
        .alarmEnabled(updateInfo.isAlarmEnabled())
        .createdAt(LocalDateTime.now())
        .build();

    given(memberService.updateMemberInfo(any(MemberRequest.UpdateInfo.class)))
        .willReturn(updatedMemberResponse);

    //when
    //then
    mockMvc.perform(post("/api/members")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(updateInfo)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(updateInfo.getName()))
        .andExpect(jsonPath("$.favorDrinkType[0]").value(updateInfo.getFavorDrinkType().get(0).toString()))
        .andExpect(jsonPath("$.alarmEnabled").value(updateInfo.isAlarmEnabled()))
        .andDo(print());
  }

  @Test
  @DisplayName("회원 정보 수정 실패 - 회원 정보 없음")
  public void failUpdateMemberInfo() throws Exception {
    // Given
    given(memberService.updateMemberInfo(any(MemberRequest.UpdateInfo.class)))
        .willThrow(new CustomException(MEMBER_NOT_FOUND));

    //when
    //then
    mockMvc.perform(post("/api/members")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(updateInfo)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(MEMBER_NOT_FOUND.getMessage()))
        .andDo(print());
  }
}