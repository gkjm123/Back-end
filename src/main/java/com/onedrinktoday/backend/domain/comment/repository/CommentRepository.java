package com.onedrinktoday.backend.domain.comment.repository;

import com.onedrinktoday.backend.domain.comment.entity.Comment;
import com.onedrinktoday.backend.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> findByPostId(Long postId, Pageable pageable);

  List<Comment> findAllByMember(Member member);
}