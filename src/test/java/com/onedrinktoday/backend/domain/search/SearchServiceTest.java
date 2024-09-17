package com.onedrinktoday.backend.domain.search;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.postTag.repository.PostTagRepository;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

  @Mock
  private PostTagRepository postTagRepository;

  @Mock
  private ElasticSearchRepository elasticSearchRepository;

  @InjectMocks
  private SearchService searchService;

  @Test
  void save() {
    //given
    Drink drink = Drink.builder()
        .id(1L)
        .name("특산주")
        .build();

    Post post = Post.builder()
        .id(1L)
        .drink(drink)
        .build();

    List<Tag> tags = List.of(new Tag(1L, "태그"));

    given(postTagRepository.findTagsByPostId(anyLong()))
        .willReturn(tags);

    given(elasticSearchRepository.save(any(PostDocument.class)))
        .willReturn(null);

    //when
    searchService.save(post);
  }
}