package com.onedrinktoday.backend.domain.suggest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import com.onedrinktoday.backend.domain.suggest.service.SuggestService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuggestServiceTest {

  @Mock
  private RegionRepository regionRepository;

  @Mock
  private DrinkRepository drinkRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private SuggestService suggestService;

  @Test
  void suggestDrinkByLocation() {
    // given
    Long memberId = 1L;
    float latitude = 37.5665f; // 서울의 위도
    float longitude = 126.9780f; // 서울의 경도

    Member member = Member.builder().id(memberId).build();

    Region seoul = Region.builder().id(1L).placeName("서울").latitude(37.5665).longitude(126.9780).build();
    Region busan = Region.builder().id(2L).placeName("부산").latitude(35.1796).longitude(129.0756).build();
    List<Region> regions = List.of(seoul, busan);

    Drink drink = Drink.builder().id(1L).region(seoul).name("서울 특산주").build();
    List<Drink> drinks = List.of(drink);

    // Mock 리턴값 설정
    given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
    given(regionRepository.findAll()).willReturn(regions);
    given(drinkRepository.findByRegion(any(Region.class))).willReturn(drinks);

    // when
    DrinkResponse result = suggestService.suggestDrinkByLocation(memberId, latitude, longitude);

    // then
    assertNotNull(result);
    assertEquals("서울", result.getPlaceName());
    assertEquals("서울 특산주", result.getName());
  }

  @Test
  void suggestDrinkForCurrentRegion() {
    // given
    Long memberId = 1L;

    Region seoul = Region.builder().id(1L).placeName("서울").latitude(37.5665).longitude(126.9780).build();
    Drink drink = Drink.builder().id(1L).region(seoul).name("서울 특산주").build();
    List<Drink> drinks = List.of(drink);

    Member member = Member.builder().id(memberId).region(seoul).build();

    // Mock 리턴값 설정
    given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
    given(drinkRepository.findByRegion(any(Region.class))).willReturn(drinks);

    // when
    DrinkResponse result = suggestService.suggestDrinkByCurrentRegion(memberId);

    // then
    assertNotNull(result);
    assertEquals("서울", result.getPlaceName());
    assertEquals("서울 특산주", result.getName());
  }
}
