package com.onedrinktoday.backend.domain.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.type.DrinkType;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

  @Mock
  private DrinkRepository drinkRepository;

  @Mock
  private RegistrationRepository registrationRepository;

  @InjectMocks
  private ManagerService managerService;

  @Test
  void approveRegistration() {
    //given
    Registration registration = Registration.builder()
        .id(1L)
        .member(new Member())
        .region(new Region())
        .drinkName("특산주")
        .type(DrinkType.BEER)
        .degree(1.1f)
        .sweetness(1)
        .cost(1000)
        .description("특산주입니다.")
        .build();

    Drink drink = Drink.builder()
        .id(1L)
        .region(new Region())
        .name("특산주")
        .type(DrinkType.BEER)
        .degree(1.1f)
        .sweetness(1)
        .cost(1000)
        .description("특산주입니다.")
        .build();

    given(registrationRepository.findById(anyLong()))
        .willReturn(Optional.of(registration));

    given(registrationRepository.save(any(Registration.class)))
        .willReturn(registration);

    given(drinkRepository.save(any(Drink.class)))
        .willReturn(drink);

    //when
    DrinkResponse drinkResponse = managerService.approveRegistration(1L);

    //then
    assertEquals(drinkResponse.getName(), "특산주");
  }

  @Test
  void cancelRegistration() {
    //given
    Registration registration = Registration.builder()
        .id(1L)
        .member(new Member())
        .region(new Region())
        .drinkName("특산주")
        .type(DrinkType.BEER)
        .degree(1.1f)
        .sweetness(1)
        .cost(1000)
        .description("특산주입니다.")
        .build();

    given(registrationRepository.findById(anyLong()))
        .willReturn(Optional.of(registration));

    given(registrationRepository.save(any(Registration.class)))
        .willReturn(registration);

    //when
    managerService.cancelRegistration(1L);

    assert true;
  }



}