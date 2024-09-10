package com.onedrinktoday.backend.domain.suggest.repository;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestRepository extends JpaRepository<Drink, Long> {

  // 생일이 오늘인 사용자 찾기
  @Query("SELECT m FROM Member m WHERE FUNCTION('DATE', m.birthDate) = FUNCTION('DATE', CURRENT_DATE)")
  List<Member> findAllByBirthDate(Date birthDate);

  // 특정 지역에서 임의의 특산주 3개를 선택하는 메서드
  @Query("SELECT d FROM Drink d WHERE d.region.id = :regionId ORDER BY FUNCTION('RAND')")
  List<Drink> findRandomDrink(@Param("regionId") Long regionId, Pageable pageable);
}
