package com.onedrinktoday.backend.domain.member.service;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

import com.onedrinktoday.backend.domain.comment.entity.Comment;
import com.onedrinktoday.backend.domain.comment.repository.CommentRepository;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignIn;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.SignUp;
import com.onedrinktoday.backend.domain.member.dto.MemberRequest.UpdateInfo;
import com.onedrinktoday.backend.domain.member.dto.MemberResponse;
import com.onedrinktoday.backend.domain.member.entity.Member;
import com.onedrinktoday.backend.domain.member.repository.MemberRepository;
import com.onedrinktoday.backend.domain.post.entity.Post;
import com.onedrinktoday.backend.domain.post.repository.PostRepository;
import com.onedrinktoday.backend.domain.registration.entity.Registration;
import com.onedrinktoday.backend.domain.registration.repository.RegistrationRepository;
import com.onedrinktoday.backend.domain.tagFollow.repository.TagFollowRepository;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import com.onedrinktoday.backend.global.security.JwtProvider;
import com.onedrinktoday.backend.global.security.MemberDetail;
import com.onedrinktoday.backend.global.security.TokenDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final TagFollowRepository tagFollowRepository;
  private final RegistrationRepository registrationRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JwtProvider jwtProvider;
  private final EmailService emailService;

  public void validateEmail(String email) {
    if (memberRepository.findByEmail(email).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_EXIST);
    }
  }

  public MemberResponse signUp(SignUp request) {

    if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_EXIST);
    }

    Member member = Member.from(request);
    member.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

    return MemberResponse.from(memberRepository.save(member));
  }

  public TokenDTO signIn(SignIn request) {
    Member member = memberRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAIL));

    if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_FAIL);
    }

    String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(),
        member.getRole());
    String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail(),
        member.getRole());

    member.setRefreshToken(refreshToken);
    memberRepository.save(member);

    return TokenDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public TokenDTO refreshAccessToken(String refreshToken) {

    String email;

    try {
      //유효기간 만료시 exception 발생
      email = jwtProvider.getEmail(refreshToken);

    } catch (SignatureException | UnsupportedJwtException | ExpiredJwtException
             | MalformedJwtException e) {
      throw new CustomException(ErrorCode.TOKEN_EXPIRED);
    }

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    //DB의 리프레시 토큰과 일치하는지 체크
    if (!refreshToken.equals(member.getRefreshToken())) {
      throw new CustomException(ErrorCode.TOKEN_NOT_MATCH);
    }

    String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(),
        member.getRole());

    return TokenDTO.builder()
        .refreshToken(refreshToken)
        .accessToken(accessToken)
        .build();
  }

  public MemberResponse getMemberInfo() {

    return MemberResponse.from(getMember());
  }

  public MemberResponse updateMemberInfo(UpdateInfo updateInfo) {
    Member member = getMember();

    if (isNotEmpty(updateInfo.getName())) {
      member.setName(updateInfo.getName());
    }

    if (updateInfo.getFavorDrinkType() != null && !updateInfo.getFavorDrinkType().isEmpty()) {
      member.setFavorDrinkType(updateInfo.getFavorDrinkType());
    }

    if (updateInfo.isAlarmEnabled() != member.isAlarmEnabled()) {
      member.setAlarmEnabled(updateInfo.isAlarmEnabled());
    }

    return MemberResponse.from(memberRepository.save(member));
  }

  //멤버 정보 필요시 MemberService 주입받아 메서드 사용
  public Member getMember() {

    MemberDetail memberDetail =
        (MemberDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return memberRepository.findByEmail(memberDetail.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }


  // 비밀번호 재설정 토큰 생성 및 이메일 전송(회원이 비밀번호를 모를 경우)
  public void requestPasswordReset(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

    String token = jwtProvider.createResetToken(member.getId(), member.getEmail(),
        member.getRole());

    // 도메인 변경(예정)
    String resetLink = "http://localhost:8080/api/members/reset-password?token=" + token;

    emailService.sendPasswordResetEmail(member.getEmail(), resetLink);
  }

  // 비밀번호 재설정(회원이 비밀번호를 모를 경우)
  public void resetPassword(String token, String newPassword) {
    String email = jwtProvider.getEmail(token);

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

    member.setPassword(bCryptPasswordEncoder.encode(newPassword));
    memberRepository.save(member);
  }

  // 비밀번호 재설정(회원이 비밀번호를 알 경우)
  public void changePassword(String email, String currentPassword, String newPassword) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

    // 현재 비밀번호 확인
    if (!bCryptPasswordEncoder.matches(currentPassword, member.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_FAIL);
    }

    // 새 비밀번호와 기존 비밀번호 동일인지 확인
    if (bCryptPasswordEncoder.matches(newPassword, member.getPassword())) {
      throw new CustomException(ErrorCode.SAME_PASSWORD);
    }

    member.setPassword(bCryptPasswordEncoder.encode(newPassword));
    memberRepository.save(member);
  }

  @Transactional
  public void withdrawMember() {
    Member member = getMember();

    tagFollowRepository.deleteByMember(member);

    handleEntitiesForWithdrawMember(member);

    memberRepository.delete(member);
  }

  private void handleEntitiesForWithdrawMember(Member member) {
    List<Post> posts = postRepository.findAllByMember(member);
    if (!posts.isEmpty()) {
      posts.forEach(post -> post.setMember(null));
      postRepository.saveAll(posts);
    }

    List<Comment> comments = commentRepository.findAllByMember(member);
    if (!comments.isEmpty()) {
      comments.forEach(comment -> comment.setMember(null));
      commentRepository.saveAll(comments);
    }

    List<Registration> registrations = registrationRepository.findAllByMember(member);
    if (!registrations.isEmpty()) {
      registrations.forEach(registration -> registration.setMember(null));
      registrationRepository.saveAll(registrations);
    }
  }

  public MemberResponse updateMemberProfile(String url) {
    Member member = getMember();
    member.setImageUrl(url);
    return MemberResponse.from(memberRepository.save(member));
  }
}
