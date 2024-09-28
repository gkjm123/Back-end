package com.onedrinktoday.backend.global.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.global.type.Role;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final MemberRepository memberRepository;
  private final JwtProvider jwtProvider;

  @Value("${oauth2.google.client-id}")
  private String clientId;

  @Value("${oauth2.google.client-secret}")
  private String clientSecret;

  @Value("${oauth2.google.redirect-uri}")
  private String redirectUri;

  @Value("${oauth2.google.token-uri}")
  private String tokenUri;

  @Value("${oauth2.google.resource-uri}")
  private String resourceUri;

  public TokenDTO join(String code) {
    String token = getAccessToken(code);
    JsonNode userResourceNode = getUserResource(token);

    String id = userResourceNode.get("id").asText();
    String email = userResourceNode.get("email").asText();
    String name = userResourceNode.get("name").asText();

    Optional<Member> optionalMember = memberRepository.findByEmail(email.trim());

    Member member = optionalMember.orElseGet(() -> memberRepository.save(Member.builder()
        .name(name)
        .email(email)
        .password(id)
        .role(Role.USER)
        .alarmEnabled(true)
        .build()));

    String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
    String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail(), member.getRole());

    member.setRefreshToken(refreshToken);
    memberRepository.save(member);

    return TokenDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  private String getAccessToken(String code) {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity entity = new HttpEntity(params, headers);

    JsonNode accessTokenNode =
        restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class).getBody();

    return accessTokenNode.get("access_token").asText();
  }

  private JsonNode getUserResource(String accessToken) {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity entity = new HttpEntity(headers);
    return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
  }
}
