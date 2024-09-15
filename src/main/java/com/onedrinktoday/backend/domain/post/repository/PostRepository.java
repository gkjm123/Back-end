package com.onedrinktoday.backend.domain.post.repository;

import com.onedrinktoday.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query(value = "select AVG(rating) from post where drink_id = :drinkId", nativeQuery = true)
  Double getAverageRating(Long drinkId);

  // 최신순으로 정렬
  Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

  // 조회수 순으로 정렬
  Page<Post> findAllByOrderByViewCountDesc(Pageable pageable);
}
