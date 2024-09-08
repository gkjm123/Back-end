package com.onedrinktoday.backend.domain.postTag.repository;

import com.onedrinktoday.backend.domain.postTag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

}
