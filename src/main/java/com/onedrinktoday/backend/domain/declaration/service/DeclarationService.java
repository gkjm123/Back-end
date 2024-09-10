package com.onedrinktoday.backend.domain.declaration.service;

import static com.onedrinktoday.backend.global.exception.ErrorCode.DECLARATION_NOT_FOUND;

import com.onedrinktoday.backend.domain.declaration.dto.DeclarationRequest;
import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import com.onedrinktoday.backend.domain.declaration.repository.DeclarationRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeclarationService {

  private final DeclarationRepository declarationRepository;
  private final MemberService memberService;

  public DeclarationResponse createDeclaration(DeclarationRequest declarationRequest) {

    Member member = memberService.getMember();

    Declaration declaration = Declaration.from(declarationRequest);
    declaration.setMember(member);

    return DeclarationResponse.from(declarationRepository.save(declaration));
  }

  public Page<DeclarationResponse> getDeclarations(Pageable pageable) {

    return declarationRepository.findAll(pageable).map(DeclarationResponse::from);
  }

  public DeclarationResponse getDeclaration(Long declarationId) {

    Declaration declaration = declarationRepository.findById(declarationId)
        .orElseThrow(() -> new CustomException(DECLARATION_NOT_FOUND));

    return DeclarationResponse.from(declaration);
  }
}
