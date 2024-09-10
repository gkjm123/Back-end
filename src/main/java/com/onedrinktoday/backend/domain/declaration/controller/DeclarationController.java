package com.onedrinktoday.backend.domain.declaration.controller;

import com.onedrinktoday.backend.domain.declaration.dto.DeclarationRequest;
import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.declaration.service.DeclarationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DeclarationController {

  private final DeclarationService declarationService;

  @PostMapping("/declarations")
  public ResponseEntity<DeclarationResponse> createDeclaration(
      @Valid @RequestBody DeclarationRequest declarationRequest
  ) {
    return ResponseEntity.ok(declarationService.createDeclaration(declarationRequest));
  }

  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/declarations")
  public ResponseEntity<Page<DeclarationResponse>> getDeclarations(
      @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(declarationService.getDeclarations(pageable));
  }

  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/declarations/{declarationId}")
  public ResponseEntity<DeclarationResponse> getDeclaration(@PathVariable Long declarationId) {
    return ResponseEntity.ok(declarationService.getDeclaration(declarationId));
  }

}