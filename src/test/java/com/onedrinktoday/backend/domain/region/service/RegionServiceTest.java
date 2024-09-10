package com.onedrinktoday.backend.domain.region.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.onedrinktoday.backend.domain.region.dto.RegionRequest;
import com.onedrinktoday.backend.domain.region.dto.RegionResponse;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegionServiceTest {

  @InjectMocks
  private RegionService regionService;

  @Mock
  private RegionRepository regionRepository;

  private Region region;
  private RegionRequest regionRequest;

  @BeforeEach
  void setUp() {
    region = Region.builder()
        .id(1L)
        .placeName("서울특별시")
        .build();

    regionRequest = RegionRequest.builder()
        .placeName("서울특별시")
        .build();
  }

  @Test
  @DisplayName("지역 저장 성공")
  void successCreateRegion() {
    // given
    when(regionRepository.findByPlaceName(regionRequest.getPlaceName()))
        .thenReturn(Optional.empty());
    when(regionRepository.save(any(Region.class))).thenReturn(region);

    // when
    RegionResponse response = regionService.createRegion(regionRequest);

    // then
    assertEquals(region.getId(), response.getId());
    assertEquals(region.getPlaceName(), response.getPlaceName());
  }

  @Test
  @DisplayName("지역 저장 실패 - 지역이 이미 존재")
  void failCreateRegion() {
    // given
    when(regionRepository.findByPlaceName(regionRequest.getPlaceName()))
        .thenReturn(Optional.of(region));

    // then
    // when
    assertThrows(CustomException.class, () -> regionService.createRegion(regionRequest));
  }

  @Test
  @DisplayName("모든 지역 조회 성공")
  void successGetRegions() {
    // given
    when(regionRepository.findAll()).thenReturn(List.of(region));

    // when
    List<RegionResponse> response = regionService.getRegions();

    // then
    assertEquals(region.getId(), response.get(0).getId());
    assertEquals(region.getPlaceName(), response.get(0).getPlaceName());
  }

  @Test
  @DisplayName("모든 지역 조회 실패 - 지역을 찾을 수가 없음")
  void failGetRegions() {
    // given
    when(regionRepository.findAll()).thenThrow(new CustomException(ErrorCode.REGION_NOT_FOUND));

    // then
    // when
    assertThrows(CustomException.class, () -> regionService.getRegions());
  }

  @Test
  @DisplayName("지역 조회 성공")
  void successGetRegion() {
    // given
    when(regionRepository.findById(region.getId())).thenReturn(Optional.of(region));

    // when
    RegionResponse response = regionService.getRegion(region.getId());

    // then
    assertEquals(region.getId(), response.getId());
    assertEquals(region.getPlaceName(), response.getPlaceName());
  }

  @Test
  @DisplayName("지역 조회 실패 - 지역이 존재하지 않음")
  void failGetRegion() {
    // given
    when(regionRepository.findById(region.getId())).thenThrow(
        new CustomException(ErrorCode.REGION_NOT_FOUND));

    // when
    // then
    assertThrows(CustomException.class, () -> regionService.getRegion(region.getId()));
  }
}