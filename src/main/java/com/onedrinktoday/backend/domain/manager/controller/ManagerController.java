package com.onedrinktoday.backend.domain.manager.controller;

import com.onedrinktoday.backend.domain.declaration.dto.DeclarationResponse;
import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.manager.dto.cancelDeclarationRequest;
import com.onedrinktoday.backend.domain.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('MANAGER')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ManagerController {

  private final ManagerService managerService;

  @PutMapping("/manager/registrations/{registId}/approve")
  public ResponseEntity<DrinkResponse> approveRegistration(@PathVariable Long registId) {

    return ResponseEntity.ok(managerService.approveRegistration(registId));
  }

  @PutMapping("/manager/registrations/{registId}/cancel")
  public ResponseEntity<String> cancelRegistration(@PathVariable Long registId) {

    managerService.cancelRegistration(registId);
    return ResponseEntity.ok("특산주 신청 반려");
  }

  @PutMapping("/manager/declarations/{declarationId}/approve")
  public ResponseEntity<DeclarationResponse> approveDeclaration(@PathVariable Long declarationId) {

    return ResponseEntity.ok(managerService.approveDeclaration(declarationId));
  }

  @PutMapping("/manager/declarations/{declarationId}/cancel")
  public ResponseEntity<DeclarationResponse> cancelDeclaration(@PathVariable Long declarationId,
      @RequestBody cancelDeclarationRequest cancelDeclarationRequest) {

    return ResponseEntity.ok(
        managerService.cancelDeclaration(declarationId, cancelDeclarationRequest));
  }

}
