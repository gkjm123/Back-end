package com.onedrinktoday.backend.domain.manager.service;

import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.domain.declaration.repository.DeclarationRepository;
import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerService {

  private final DrinkRepository drinkRepository;
  private final RegistrationRepository registrationRepository;
  private final DeclarationRepository declarationRepository;

  @Transactional
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

  public DeclarationResponse approveDeclaration(Long declarationId) {

    Declaration declaration = declarationRepository.findById(declarationId)
        .orElseThrow(() -> new CustomException(ErrorCode.DECLARATION_NOT_FOUND));

    declaration.setApproved(true);

    return DeclarationResponse.from(declarationRepository.save(declaration));
  }

  public DeclarationResponse cancelDeclaration(Long declarationId) {

    Declaration declaration = declarationRepository.findById(declarationId)
        .orElseThrow(() -> new CustomException(ErrorCode.DECLARATION_NOT_FOUND));

    declaration.setApproved(false);

    return DeclarationResponse.from(declarationRepository.save(declaration));
  }
}
