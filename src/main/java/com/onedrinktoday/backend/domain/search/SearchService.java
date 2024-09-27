package com.onedrinktoday.backend.domain.search;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.onedrinktoday.backend.domain.drink.dto.DrinkResponse;
import com.onedrinktoday.backend.domain.drink.repository.DrinkRepository;
import com.onedrinktoday.backend.domain.post.dto.PostResponse;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.postTag.repository.PostTagRepository;
import com.onedrinktoday.backend.domain.tag.entity.Tag;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final ElasticsearchOperations elasticsearchOperations;
  private final ElasticSearchRepository elasticSearchRepository;
  private final PostTagRepository postTagRepository;
  private final PostRepository postRepository;
  private final DrinkRepository drinkRepository;

  public void save(Post post, List<Tag> tagList) {

    String tags = String.join(" ", tagList.stream()
        .map(t -> StringUtils.trimAllWhitespace(t.getTagName())).toList());

    PostDocument postDocument = PostDocument.builder()
        .id(post.getId())
        .tags(tags)
        .drink(StringUtils.trimAllWhitespace(post.getDrink().getName()))
        .build();

    elasticSearchRepository.save(postDocument);
  }

  public void delete(Post post) {
    elasticSearchRepository.deleteById(post.getId());
  }

  public Page<PostResponse> searchPostByTag(Pageable pageable, List<String> tagList) {

    String tags = String.join(" ", tagList.stream()
        .map(StringUtils::trimAllWhitespace).toList());

    Query query = QueryBuilders.match()
        .query(tags)
        .field("tags")
        .operator(Operator.And)
        .build()._toQuery();

    return getPostResponses(pageable, query);
  }

  public Page<PostResponse> searchPostByDrink(Pageable pageable, String drink) {

    Query query = QueryBuilders.match()
        .query(StringUtils.trimAllWhitespace(drink))
        .field("drink")
        .build()._toQuery();

    return getPostResponses(pageable, query);
  }

  private Page<PostResponse> getPostResponses(Pageable pageable, Query query) {

    NativeQuery nativeQuery = new NativeQueryBuilder()
        .withQuery(query)
        .withPageable(pageable)
        .build();

    SearchHits<PostDocument> searchHits =
        elasticsearchOperations.search(nativeQuery, PostDocument.class);

    List<PostResponse> postResponses = searchHits.get()
        .map(s -> postRepository.findById(s.getContent().getId()).orElse(null))
        .filter(Objects::nonNull)
        .map(post -> {
          List<Tag> tags = postTagRepository.findTagsByPostId(post.getId());

          return PostResponse.of(post, tags, false);
        })
        .toList();

    return new PageImpl<>(postResponses, pageable, searchHits.getTotalHits());
  }

  public Page<DrinkResponse> searchDrink(Pageable pageable, Long regionId, String drinkName) {

    if (regionId == 0) {
      return drinkRepository.findAllByNameContaining(pageable, drinkName).map(DrinkResponse::from);
    } else {
      return drinkRepository.findAllByRegion_IdAndNameContaining(pageable, regionId, drinkName)
          .map(DrinkResponse::from);
    }
  }
}
