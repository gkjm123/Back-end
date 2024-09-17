package com.onedrinktoday.backend.domain.tagFollow.repository;

import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tagFollow.entity.TagFollow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagFollowRepository extends JpaRepository<TagFollow, Long> {

  List<TagFollow> findByMember(Member member);

  boolean existsByMemberAndTag(Member member, Tag tag);

  List<TagFollow> findByTag(Tag tag);

  void deleteByMember(Member member);
}