package com.onedrinktoday.backend.domain.registration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.domain.registration.dto.RegistrationRequest;
import com.onedrinktoday.backend.domain.registration.dto.RegistrationResponse;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.type.Role;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock
  private RegistrationRepository registrationRepository;

  @Mock
  private MemberService memberService;

  @Mock
  private RegionRepository regionRepository;

  @InjectMocks
  private RegistrationService registrationService;

  @Test
  void register() {
    //given
    Member member = Member.builder()
        .id(1L)
        .name("멤버")
        .role(Role.USER)
        .build();

    Region region = Region.builder()
        .id(1L)
        .placeName("서울")
        .longitude(123.123)
        .latitude(456.456)
        .build();

    RegistrationRequest request = RegistrationRequest.builder()
        .drinkName("특산주")
        .description("특산주 입니다.")
        .regionId(1L)
        .build();

    given(memberService.getMember())
        .willReturn(member);

    given(regionRepository.findById(eq(1L)))
        .willReturn(Optional.of(region));

    ArgumentCaptor<Registration> registrationCaptor = ArgumentCaptor.forClass(Registration.class);

    given(registrationRepository.save(registrationCaptor.capture()))
        .willReturn(Registration.builder()
            .id(1L)
            .member(member)
            .region(region)
            .drinkName("특산주")
            .description("특산주 신청입니다.")
            .build()
        );

    //when
    RegistrationResponse response = registrationService.register(request);

    //then
    assertEquals(response.getDrinkName(), "특산주");
    assertEquals(registrationCaptor.getValue().getDrinkName(), "특산주");
  }
}