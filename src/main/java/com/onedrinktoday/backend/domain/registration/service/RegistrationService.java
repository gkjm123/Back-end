package com.onedrinktoday.backend.domain.registration.service;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.service.MemberService;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.domain.registration.dto.RegistrationRequest;
import com.onedrinktoday.backend.domain.registration.dto.RegistrationResponse;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final RegistrationRepository registrationRepository;
  private final MemberService memberService;
  private final RegionRepository regionRepository;

  public RegistrationResponse register(RegistrationRequest request) {

    Registration registration = Registration.from(request);

    Member member = memberService.getMember();

    Region region = regionRepository.findById(request.getRegionId())
        .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));

    registration.setMember(member);
    registration.setRegion(region);

    return RegistrationResponse.from(registrationRepository.save(registration));
  }

  public Page<RegistrationResponse> getRegistrations(Pageable pageable) {

    return registrationRepository.findAll(pageable).map(RegistrationResponse::from);
  }

  public RegistrationResponse getRegistration(Long registId) {

    Registration registration = registrationRepository.findById(registId)
        .orElseThrow(() -> new CustomException(ErrorCode.REGISTRATION_NOT_FOUND));

    return RegistrationResponse.from(registration);
  }
}
