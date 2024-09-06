package com.onedrinktoday.backend.domain.member.service;


import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignIn;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignUp;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.UpdateInfo;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.global.security.JwtProvider;
import com.onedrinktoday.backend.global.type.Drink;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

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
  private List<Drink> favorDrinks;

  @BeforeEach
  void setUp() {
    member = new Member();
    member.setEmail("john@google.com");
    member.setPassword("Password123!");
    region = new Region();
    region.setId(1L);

    // favorDrinks 리스트 초기화
    favorDrinks = Arrays.asList(Drink.SOJU, Drink.BEER);

    signUpRequest = new SignUp(1L, "John", "john@google.com", "Password123!", new Date(), favorDrinks, true);
    signInRequest = new SignIn("john@google.com", "Password123!");
    updateInfo = new UpdateInfo(1L, "John", favorDrinks, true, "new_image_url");
  }

  /*
  @Test
  void changePasswordTest() {
    // Given

    // When

    // Then
  }
  */
}