package com.smousseur.specification.api.example.model.entity.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonConverter<T> implements AttributeConverter<Object, String> {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);

  static {
    MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.registerModule(new JavaTimeModule());
  }

  protected abstract Class<T> getClazz();

  @Override
  public String convertToDatabaseColumn(Object x) {
    try {
      return MAPPER.writeValueAsString(x);
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not convert context to json string", e);
    }
    return null;
  }

  @Override
  public Object convertToEntityAttribute(String content) {
    if (content == null) {
      return null;
    }
    try {
      return MAPPER.readValue(content, getClazz());
    } catch (JsonProcessingException e) {
      LOGGER.error("Could not convert json string to context", e);
    }
    return null;
  }
}
