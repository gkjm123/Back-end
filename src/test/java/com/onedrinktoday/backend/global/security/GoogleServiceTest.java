package com.onedrinktoday.backend.global.security;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.global.type.Role;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private JwtProvider jwtProvider;

  @InjectMocks
  private GoogleService googleService;

  @Test
  void join() {
    //given
    Member member = Member.builder()
        .email("abc@naver.com")
        .id(1L)
        .name("baby")
        .role(Role.USER)
        .build();

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    given(memberRepository.save(any()))
        .willReturn(member);

    given(jwtProvider.createAccessToken(anyString(), any()))
        .willReturn("access");

    given(jwtProvider.createRefreshToken(anyString(), any()))
        .willReturn("refresh");

    //when
    TokenDto tokenDto = googleService.join("code");

    //then
    assertEquals(tokenDto.getAccessToken(), "access");

  }

}