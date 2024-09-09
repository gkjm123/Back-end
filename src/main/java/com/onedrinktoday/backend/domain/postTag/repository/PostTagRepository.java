package com.onedrinktoday.backend.domain.postTag.repository;

import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.postTag.entity.PostTag;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

  // 특정 게시글 연결 태그 조회
  @Query("SELECT pt.tag FROM PostTag pt WHERE pt.post.id = :postId")
  List<Tag> findTagsByPostId(@Param("postId") Long postId);

  Optional<PostTag> findByPostAndTag(Post post, Tag tag);
}
