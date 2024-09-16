package com.onedrinktoday.backend.domain.suggest.repository;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestRepository extends JpaRepository<Drink, Long> {

  // 생일이 오늘인 사용자 찾기(월과 일만 비교)
  @Query("SELECT m FROM Member m WHERE FUNCTION('MONTH', m.birthDate) = :month AND FUNCTION('DAY', m.birthDate) = :day")
  List<Member> findAllByBirthDate(@Param("month") int month, @Param("day") int day);

  // 특정 지역에서 임의 특산주 3개 선택
  @Query("SELECT d FROM Drink d WHERE d.region.id = :regionId ORDER BY FUNCTION('RAND')")
  List<Drink> findRandomDrink(@Param("regionId") Long regionId, Pageable pageable);

  // 최근 1주일간 게시글의 특산주 빈도수를 기준으로 상위 20개의 특산주 조회
  @Query("SELECT d, COUNT(p) as postCount " +
      "FROM Post p JOIN p.drink d " +
      "WHERE p.createdAt >= :startDate " +
      "GROUP BY d.id " +
      "ORDER BY postCount DESC")
  List<Object[]> findTopDrinksByPostCountInLastWeek(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
