package com.onedrinktoday.backend.domain.declaration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.onedrinktoday.backend.domain.declaration.dto.DeclarationRequest;
import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.domain.declaration.repository.DeclarationRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.type.Role;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DeclarationServiceTest {

  @Mock
  private DeclarationRepository declarationRepository;

  @Mock
  private MemberService memberService;

  @InjectMocks
  private DeclarationService declarationService;

  @Test
  void createDeclaration() {
    //given
    Member member = Member.builder()
        .id(1L)
        .name("멤버")
        .role(Role.USER)
        .build();

    DeclarationRequest request = DeclarationRequest.builder()
        .link("abc")
        .type("타입")
        .content("내용")
        .build();

    given(memberService.getMember())
        .willReturn(member);

    given(declarationRepository.save(any()))
        .willReturn(Declaration.builder()
            .id(1L)
            .member(member)
            .link("abc")
            .type("타입")
            .content("내용")
            .approved(true)
            .build()
        );

    //when
    DeclarationResponse response = declarationService.createDeclaration(request);

    //then
    assertEquals(response.getContent(), "내용");

  }

  @Test
  void getDeclarations() {
    //given
    Member member = Member.builder()
        .id(1L)
        .name("멤버")
        .role(Role.USER)
        .build();

    given(declarationRepository.findAll(Pageable.ofSize(10)))
        .willReturn(
            new PageImpl<>(List.of(Declaration.builder()
                .member(member)
                .link("abc")
                .type("타입")
                .content("내용")
                .build()), Pageable.ofSize(10), 10)
        );

    //when
    Page<DeclarationResponse> declarationResponses =
        declarationService.getDeclarations(Pageable.ofSize(10));

    //then
    assertEquals(declarationResponses.getContent().get(0).getContent(), "내용");

  }

  @Test
  void getDeclaration() {
    //given
    Member member = Member.builder()
        .id(1L)
        .name("멤버")
        .role(Role.USER)
        .build();

    Declaration declaration = Declaration.builder()
        .member(member)
        .link("abc")
        .type("타입")
        .content("내용")
        .build();

    given(declarationRepository.findById(any()))
        .willReturn(Optional.of(declaration));

    //when
    DeclarationResponse response = declarationService.getDeclaration(declaration.getId());

    //then
    assertEquals(response.getType(), "타입");
  }
}