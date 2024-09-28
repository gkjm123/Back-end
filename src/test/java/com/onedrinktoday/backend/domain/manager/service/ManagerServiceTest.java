package com.onedrinktoday.backend.domain.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.onedrinktoday.backend.domain.autoComplete.AutoCompleteService;
import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.domain.declaration.repository.DeclarationRepository;
import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.manager.dto.CancelDeclarationRequest;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.notification.service.NotificationService;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.type.CancelDeclarationType;
import com.onedrinktoday.backend.global.type.DeclarationType;
import com.onedrinktoday.backend.global.type.DrinkType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

  @Mock
  private DrinkRepository drinkRepository;

  @Mock
  private RegistrationRepository registrationRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private DeclarationRepository declarationRepository;

  @Mock
  private NotificationService notificationService;

  @Mock
  private AutoCompleteService autoCompleteService;

  @InjectMocks
  private ManagerService managerService;

  private Declaration declaration;
  private Registration registration;
  private Post post;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(managerService, "postUri", "/post/{postId}");
    ReflectionTestUtils.setField(managerService, "postId", "postId");

    Member member = Member.builder()
        .id(1L)
        .name("John Doe")
        .build();

    registration = Registration.builder()
        .id(1L)
        .member(member)
        .region(new Region())
        .drinkName("특산주")
        .type(DrinkType.BEER)
        .degree(1.1f)
        .sweetness(1)
        .cost(1000)
        .description("특산주입니다.")
        .build();

    declaration = Declaration.builder()
        .id(1L)
        .member(member)
        .link("/post/1")
        .type(DeclarationType.OBSCENE)
        .content("신고내용 입니다.")
        .approved(false)
        .createdAt(LocalDateTime.now())
        .build();

    post = Post.builder()
        .id(1L)
        .build();
  }

  @Test
  @DisplayName("특산주 승인 성공")
  void approveRegistration() {
    //given
    given(registrationRepository.findById(1L)).willReturn(Optional.of(registration));
    given(registrationRepository.save(registration)).willReturn(registration);

    Drink drink = Drink.builder()
        .id(1L)
        .region(registration.getRegion())
        .name("특산주")
        .type(DrinkType.BEER)
        .degree(1.1f)
        .sweetness(1)
        .cost(1000)
        .description("특산주입니다.")
        .build();

    given(drinkRepository.save(argThat(savedDrink ->
        savedDrink.getName().equals("특산주") &&
            savedDrink.getRegion().equals(registration.getRegion()) &&
            savedDrink.getType() == DrinkType.BEER
    ))).willReturn(drink);

    doNothing().when(notificationService)
        .approveRegistrationNotification(eq(registration.getMember()), eq(registration));
    doNothing().when(autoCompleteService).saveAutoCompleteDrink(eq(drink.getName()));

    //when
    DrinkResponse drinkResponse = managerService.approveRegistration(1L);

    //then
    assertEquals(drinkResponse.getName(), "특산주");
  }

  @Test
  @DisplayName("특산주 반려 성공")
  void cancelRegistration() {
    //given
    given(registrationRepository.findById(1L)).willReturn(Optional.of(registration));
    given(registrationRepository.save(eq(registration))).willReturn(registration);

    //when
    managerService.cancelRegistration(1L);

    assert true;
  }

  @Test
  @DisplayName("신고 승인 성공")
  void approveDeclaration() {
    //given
    given(declarationRepository.findById(1L)).willReturn(Optional.of(declaration));
    given(postRepository.findById(1L)).willReturn(Optional.of(post));
    doNothing().when(notificationService).approveDeclarationNotification(eq(post), eq(declaration));

    given(declarationRepository.save(eq(declaration))).willAnswer(invocation -> {
      Declaration savedDeclaration = invocation.getArgument(0);
      savedDeclaration.setApproved(true);
      return savedDeclaration;
    });

    //when
    DeclarationResponse declarationResponse = managerService.approveDeclaration(1L);

    //then
    assertEquals(declaration.getId(), declarationResponse.getId());
    assertTrue(declarationResponse.getApproved());
    assertTrue(declaration.getApproved());
  }

  @Test
  @DisplayName("신고 반려 성공")
  void cancelDeclaration() {
    //given
    CancelDeclarationRequest cancelDeclarationRequest = CancelDeclarationRequest.builder()
        .type(CancelDeclarationType.POST_DELETED_BY_USER)
        .build();

    declaration.setApproved(true);

    given(declarationRepository.findById(1L)).willReturn(Optional.of(declaration));
    doNothing().when(notificationService)
        .cancelDeclarationNotification(eq(declaration), eq(cancelDeclarationRequest));
    given(declarationRepository.save(eq(declaration))).willReturn(declaration);

    //when
    DeclarationResponse declarationResponse = managerService.cancelDeclaration(1L,
        cancelDeclarationRequest);

    //then
    assertEquals(declaration.getId(), declarationResponse.getId());
    assertFalse(declarationResponse.getApproved());
  }
}