package com.onedrinktoday.backend.domain.tag.repository;

import com.onedrinktoday.backend.domain.tag.entity.Tag;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByTagName(String tagName);

  // 최근 1주일 상위 20개 태그 가져오는 쿼리
  @Query("SELECT t, COUNT(pt.post) as postCount " +
          "FROM Tag t " +
          "JOIN PostTag pt ON t.tagId = pt.tag.tagId " +
          "JOIN Post p ON pt.post.id = p.id " +
          "WHERE p.createdAt >= :startDate " +
          "GROUP BY t.tagId " +
          "ORDER BY postCount DESC")
  List<Object[]> findTopTagsByPostCount(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
