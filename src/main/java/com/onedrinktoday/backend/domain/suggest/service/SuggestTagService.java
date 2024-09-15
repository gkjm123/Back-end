package com.onedrinktoday.backend.domain.suggest.service;

import com.onedrinktoday.backend.domain.tag.dto.TagDTO;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import com.onedrinktoday.backend.domain.tag.repository.TagRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestTagService {
  private final TagRepository tagRepository;

  // 상위 20개 태그 중 랜덤으로 15개 반환
  public List<TagDTO> getRandomTopTags() {
    // 1주일 전 날짜 계산
    LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);

    // 상위 20개 태그 가져오기
    List<Object[]> topTagsPostCount = tagRepository.findTopTagsByPostCount(weekAgo, PageRequest.of(0, 20));

    // 태그 객체 리스트로 변환
    List<Tag> topTags = topTagsPostCount.stream()
        .map(result -> (Tag) result[0])
        .collect(Collectors.toList());

    // 상위 태그 중 랜덤 15개 선택
    Collections.shuffle(topTags);
    List<Tag> randomTopTags = topTags.stream().limit(15).collect(Collectors.toList());

    return randomTopTags.stream().map(TagDTO::from).collect(Collectors.toList());
  }
}
