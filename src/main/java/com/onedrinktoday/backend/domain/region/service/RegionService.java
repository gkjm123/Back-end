package com.onedrinktoday.backend.domain.region.service;

import com.onedrinktoday.backend.domain.region.dto.RegionRequest;
import com.onedrinktoday.backend.domain.region.dto.RegionResponse;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

  private final RegionRepository regionRepository;

  public RegionResponse createRegion(RegionRequest request) {

    if (regionRepository.findByPlaceName(request.getPlaceName()).isPresent()) {
      throw new CustomException(ErrorCode.REGION_EXIST);
    }

    return RegionResponse.from(regionRepository.save(Region.from(request)));
  }

  public List<RegionResponse> getRegions() {

    List<Region> regions = regionRepository.findAll();
    return regions.stream().map(RegionResponse::from).toList();
  }

  public RegionResponse getRegion(Long regionId) {

    return RegionResponse.from(
        regionRepository.findById(regionId)
            .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND))
    );
  }
}
