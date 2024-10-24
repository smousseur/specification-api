package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

public interface JsonExpression<T> {
  <Z, X> Expression<T> getExpression(
      CriteriaJsonValue<T> criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder);

  default String getNormalizedJsonPath(String jsonPath) {
    return "$." + jsonPath;
  }
}
