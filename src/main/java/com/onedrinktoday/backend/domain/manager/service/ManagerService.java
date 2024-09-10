package com.onedrinktoday.backend.domain.manager.service;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerService {

  private final DrinkRepository drinkRepository;
  private final RegistrationRepository registrationRepository;

  public DrinkResponse approveRegistration(Long registId) {

    Registration registration = registrationRepository.findById(registId)
        .orElseThrow(() -> new CustomException(ErrorCode.REGISTRATION_NOT_FOUND));

    registration.setApproved(true);
    registrationRepository.save(registration);

    Drink drink = Drink.builder()
        .region(registration.getRegion())
        .name(registration.getDrinkName())
        .type(registration.getType())
        .degree(registration.getDegree())
        .sweetness(registration.getSweetness())
        .cost(registration.getCost())
        .description(registration.getDescription())
        .imageUrl(registration.getImageUrl())
        .build();

    return DrinkResponse.from(drinkRepository.save(drink));
  }

  public void cancelRegistration(Long registId) {

    Registration registration = registrationRepository.findById(registId)
        .orElseThrow(() -> new CustomException(ErrorCode.REGISTRATION_NOT_FOUND));

    registration.setApproved(false);
    registrationRepository.save(registration);
  }
}
