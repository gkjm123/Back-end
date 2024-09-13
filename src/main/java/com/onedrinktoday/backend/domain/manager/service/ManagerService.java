package com.onedrinktoday.backend.domain.manager.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.*;
import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.domain.declaration.repository.DeclarationRepository;
import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.notification.service.NotificationService;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerService {

  private final DrinkRepository drinkRepository;
  private final RegistrationRepository registrationRepository;
  private final DeclarationRepository declarationRepository;
  private final PostRepository postRepository;
  private final NotificationService notificationService;

  @Transactional
  public DrinkResponse approveRegistration(Long registId) {

    Registration registration = registrationRepository.findById(registId)
        .orElseThrow(() -> new CustomException(REGISTRATION_NOT_FOUND));

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
        .orElseThrow(() -> new CustomException(REGISTRATION_NOT_FOUND));

    registration.setApproved(false);
    registrationRepository.save(registration);
  }

  public DeclarationResponse approveDeclaration(Long declarationId) {
    Declaration declaration = declarationRepository.findById(declarationId)
        .orElseThrow(() -> new CustomException(DECLARATION_NOT_FOUND));

    Post post = postRepository.findById(postIdFromLink(declaration.getLink()))
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    postRepository.delete(post);

    notificationService.postDeclarationNotification(post, declaration);

    declaration.setApproved(true);
    return DeclarationResponse.from(declarationRepository.save(declaration));
  }

  public DeclarationResponse cancelDeclaration(Long declarationId) {

    Declaration declaration = declarationRepository.findById(declarationId)
        .orElseThrow(() -> new CustomException(DECLARATION_NOT_FOUND));

    declaration.setApproved(false);

    return DeclarationResponse.from(declarationRepository.save(declaration));
  }

  //링크에서 게시글 ID를 확인하여 게시글를 조회
  private Long postIdFromLink(String link) {
    try {
      String[] parts = link.split("/");

      return Long.parseLong(parts[parts.length - 1]);
    } catch (CustomException e) {
      throw new CustomException(LINK_NOT_FOUND);
    }
  }
}
