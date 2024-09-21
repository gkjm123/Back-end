package com.onedrinktoday.backend.domain.aws;

import static com.onedrinktoday.backend.global.exception.ErrorCode.IMAGE_UPLOAD_FAIL;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.onedrinktoday.backend.global.exception.CustomException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String uploadAndGetUrl(MultipartFile file) {

    String originalFilename = file.getOriginalFilename() + UUID.randomUUID();
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {
      amazonS3.putObject(bucket, originalFilename, file.getInputStream(), metadata);
    } catch (IOException e) {
      throw new CustomException(IMAGE_UPLOAD_FAIL);
    }

    //저장한 이미지 url 받기
    return amazonS3.getUrl(bucket, originalFilename).toString();
  }
}
