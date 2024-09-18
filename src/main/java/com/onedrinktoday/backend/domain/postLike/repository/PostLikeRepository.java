package com.onedrinktoday.backend.domain.postLike.repository;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.postLike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
  boolean existsByPostAndMember(Post post, Member member);
  void deleteByPostAndMember(Post post, Member member);
}
