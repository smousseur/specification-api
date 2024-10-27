package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface JsonExpression {
  <T, Z, X> Expression<T> getExpression(
      CriteriaJsonValue criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder);

  @SuppressWarnings("unchecked")
  default <T, Z, X> Expression<T> getEvaluatedExpression(
      CriteriaJsonValue criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Expression<T> result = getExpression(criteriaJsonValue, from, criteriaBuilder);
    Class<T> valueClass = (Class<T>) criteriaJsonValue.value().getClass();
    if (valueClass.equals(LocalDate.class) || valueClass.equals(LocalDateTime.class)) {
      result = result.as(valueClass);
    }

    return result;
  }

  default String getNormalizedJsonPath(String jsonPath) {
    return "$." + jsonPath;
  }
}
