package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface JsonExpression<T> {
  <Z, X> Expression<T> getExpression(
      CriteriaJsonValue<T> criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder);

  default <Z, X> Expression<T> getEvaluatedExpression(
      CriteriaJsonValue<T> criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Expression<T> result = getExpression(criteriaJsonValue, from, criteriaBuilder);
    if (criteriaJsonValue.classz().equals(LocalDate.class)
        || criteriaJsonValue.classz().equals(LocalDateTime.class)) {
      result = result.as(criteriaJsonValue.classz());
    }

    return result;
  }

  default String getNormalizedJsonPath(String jsonPath) {
    return "$." + jsonPath;
  }
}
