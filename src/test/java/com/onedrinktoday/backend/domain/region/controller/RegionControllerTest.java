package com.onedrinktoday.backend.domain.region.controller;

import static com.onedrinktoday.backend.global.exception.ErrorCode.REGION_EXIST;
import static com.onedrinktoday.backend.global.exception.ErrorCode.REGION_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.domain.region.dto.RegionRequest;
import com.onedrinktoday.backend.domain.region.dto.RegionResponse;
import com.onedrinktoday.backend.domain.region.service.RegionService;
import com.onedrinktoday.backend.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(RegionController.class)
public class RegionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RegionService regionService;

  @Autowired
  private ObjectMapper objectMapper;

  private RegionRequest regionRequest;
  private RegionResponse regionResponse;

  @BeforeEach
  void setUp() {
    regionRequest = RegionRequest.builder()
        .placeName("서울특별시")
        .latitude(37.5665)
        .longitude(126.978)
        .build();
    regionResponse = RegionResponse.builder()
        .id(1L)
        .placeName("서울특별시")
        .latitude(37.5665)
        .longitude(126.978)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("지역 저장 성공")
  void successCreateRegion() throws Exception {
    //given
    given(regionService.createRegion(any(RegionRequest.class))).willReturn(regionResponse);

    //when
    //then
    mockMvc.perform(post("/api/regions")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(regionRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(regionResponse.getId()))
        .andExpect(jsonPath("$.placeName").value(regionResponse.getPlaceName()))
        .andExpect(jsonPath("$.latitude").value(regionResponse.getLatitude()))
        .andExpect(jsonPath("$.longitude").value(regionResponse.getLongitude()))
        .andDo(print());
  }

  @Test
  @DisplayName("지역 저장 실패 - 지역이 이미 존재")
  void failCreateRegion() throws Exception {
    //given
    given(regionService.createRegion(any(RegionRequest.class)))
        .willThrow(new CustomException(REGION_EXIST));

    //when
    //then
    mockMvc.perform(post("/api/regions")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(regionRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(REGION_EXIST.getMessage()))
        .andDo(print());
  }

  @Test
  @DisplayName("모든 지역 조회 성공")
  void successGetRegions() throws Exception {
    //given
    given(regionService.getRegions()).willReturn(List.of(regionResponse));

    //when
    //then
    mockMvc.perform(get("/api/regions")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(regionResponse.getId()))
        .andExpect(jsonPath("$[0].placeName").value(regionResponse.getPlaceName()))
        .andExpect(jsonPath("$[0].latitude").value(regionResponse.getLatitude()))
        .andExpect(jsonPath("$[0].longitude").value(regionResponse.getLongitude()))
        .andDo(print());
  }

  @Test
  @DisplayName("모든 지역 조회 실패 - 지역을 찾을 수가 없음")
  void failGetRegionsNoData() throws Exception {
    // given
    given(regionService.getRegions()).willReturn(List.of());

    //when
    //then
    mockMvc.perform(get("/api/regions")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"))
        .andDo(print());
  }

  @Test
  @DisplayName("단일 지역 조회 성공")
  void successGetRegion() throws Exception {
    //given
    given(regionService.getRegion(any(Long.class))).willReturn(regionResponse);

    //when
    //then
    mockMvc.perform(get("/api/regions/{regionId}", 1L)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(regionResponse.getId()))
        .andExpect(jsonPath("$.placeName").value(regionResponse.getPlaceName()))
        .andExpect(jsonPath("$.latitude").value(regionResponse.getLatitude()))
        .andExpect(jsonPath("$.longitude").value(regionResponse.getLongitude()))
        .andDo(print());
  }

  @Test
  @DisplayName("단일 지역 조회 실패 - 존재하지 않는 지역 ID")
  void failGetRegion() throws Exception {
    //given
    given(regionService.getRegion(any(Long.class)))
        .willThrow(new CustomException(REGION_NOT_FOUND));

    //when
    //then
    mockMvc.perform(get("/api/regions/{regionId}", 101010L)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(REGION_NOT_FOUND.getMessage()))
        .andDo(print());
  }
}