package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.region.entity.Region;
import com.onedrinktoday.backend.domain.region.repository.RegionRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestService {
  private final RegionRepository regionRepository;
  private final DrinkRepository drinkRepository;
  private final MemberRepository memberRepository;

  // 사용자 위치 기준 가장 가까운 지역 찾기
  public DrinkResponse suggestDrinkByLocation(Long memberId, Float latitude, Float longitude) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    List<Region> allregions = regionRepository.findAll();
    Region closestRegion = findClosestRegion(allregions, latitude, longitude);

    // 사용자 거주지값 갱신
    member.setRegion(closestRegion);
    memberRepository.save(member);

    // 해당 지역 특산주 리스트 가져와서 랜덤으로 하나 추천
    List<Drink> drinkInRegion = drinkRepository.findByRegion(closestRegion);
    return drinkInRegion.isEmpty() ? null : DrinkResponse.from(getRandomDrink(drinkInRegion));
  }

  private Region findClosestRegion(List<Region> regions, double lat, double lon) {
    return regions.stream()
        .min((r1, r2) -> {
          double dist1 = calculateDistance(lat, lon, r1.getLatitude(), r1.getLongitude());
          double dist2 = calculateDistance(lat, lon, r2.getLatitude(), r2.getLongitude());
          return Double.compare(dist1, dist2);
        })
        .orElseThrow(() -> new RuntimeException("가장 가까운 지역을 찾을 수 없습니다."));
  }

  // Haversine 공식: 두 지점 사이의 거리를 계산
  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // 지구 반경 (단위: km)
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c; // 거리 (단위 : km)
}

  // 랜덤 특산주 추천
  private Drink getRandomDrink(List<Drink> drinks) {
    Random random = new Random();
    return drinks.get(random.nextInt(drinks.size()));
  }

  // 사용자 기존 지역으로 특산주 추천
  public DrinkResponse suggestDrinkByCurrentRegion(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    Region currentRegion = member.getRegion();
    if(currentRegion == null) {
      throw new RuntimeException("사용자의 거주지 정보가 없습니다.");
    }

    List<Drink> drinksInRegion = drinkRepository.findByRegion(currentRegion);
    return drinksInRegion.isEmpty() ? null : DrinkResponse.from(getRandomDrink(drinksInRegion));
  }
}
