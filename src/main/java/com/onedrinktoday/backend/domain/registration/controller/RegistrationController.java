package com.onedrinktoday.backend.domain.registration.controller;

import com.onedrinktoday.backend.domain.registration.dto.RegistrationRequest;
import com.onedrinktoday.backend.domain.registration.dto.RegistrationResponse;
import com.onedrinktoday.backend.domain.registration.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegistrationController {

  private final RegistrationService registrationService;

  @PostMapping("/drinks/registrations")
  public ResponseEntity<RegistrationResponse> register(
      @Valid @RequestBody RegistrationRequest registrationRequest
  ) {
    return ResponseEntity.ok(registrationService.register(registrationRequest));
  }

  @GetMapping("/drinks/registrations")
  public ResponseEntity<Page<RegistrationResponse>> getRegistrations(
      @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(registrationService.getRegistrations(pageable));
  }

  @GetMapping("/drinks/registrations/{registId}")
  public ResponseEntity<RegistrationResponse> getRegistration(@PathVariable Long registId) {
    return ResponseEntity.ok(registrationService.getRegistration(registId));
  }

}