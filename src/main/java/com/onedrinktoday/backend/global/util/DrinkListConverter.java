package com.onedrinktoday.backend.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedrinktoday.backend.global.exception.CustomException;
import com.onedrinktoday.backend.global.exception.ErrorCode;
import com.onedrinktoday.backend.global.type.DrinkType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@Converter
@RequiredArgsConstructor
public class DrinkListConverter implements AttributeConverter<List<DrinkType>, String> {

  private final ObjectMapper objectMapper;

  @Override
  public String convertToDatabaseColumn(List<DrinkType> attribute) {
    if (Objects.isNull(attribute)) {
      return Strings.EMPTY;
    }
    try {
      return objectMapper.writeValueAsString(attribute);

    } catch (JsonProcessingException e) {
      throw new CustomException(ErrorCode.CONVERT_ERROR);
    }
  }

  @Override
  public List<DrinkType> convertToEntityAttribute(String dbData) {
    if (Strings.isBlank(dbData)) {
      return Collections.emptyList();
    }
    try {
      return objectMapper.readValue(dbData, new TypeReference<>() {});

    } catch (JsonProcessingException e) {
      throw new CustomException(ErrorCode.CONVERT_ERROR);
    }
  }
}