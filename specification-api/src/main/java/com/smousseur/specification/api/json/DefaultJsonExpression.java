package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

public class DefaultJsonExpression implements JsonExpression {
  private static final String JSON_VALUE = "json_value";

  @Override
  @SuppressWarnings("unchecked")
  public <T, Z, X> Expression<T> getExpression(
      CriteriaJsonValue criteriaValue, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.function(
        JSON_VALUE,
        (Class<T>) criteriaValue.value().getClass(),
        from.get(criteriaValue.path()),
        criteriaBuilder.literal(getNormalizedJsonPath(criteriaValue.jsonPath())));
  }
}
