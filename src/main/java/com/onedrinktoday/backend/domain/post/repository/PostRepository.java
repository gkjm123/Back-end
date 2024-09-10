package com.onedrinktoday.backend.domain.post.repository;

import com.onedrinktoday.backend.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Query(value = "select AVG(rating) from post where drink_id = :drinkId", nativeQuery = true)
  Float getAverageRating(Long drinkId);

}
