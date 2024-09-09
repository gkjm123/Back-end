package com.onedrinktoday.backend.domain.postTag.repository;

import com.onedrinktoday.backend.domain.postTag.entity.PostTag;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

  @Query("SELECT pt.tag FROM PostTag pt WHERE pt.postedTagId = :postId")
  List<Tag> findTagsByPostId(@Param("postId") Long postId);
}
