package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

public class DefaultJsonExpression<T> implements JsonExpression<T> {
  private static final String JSON_VALUE = "json_value";

  @Override
  public <Z, X> Expression<T> getExpression(
      CriteriaJsonValue<T> criteriaValue, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.function(
        JSON_VALUE,
        criteriaValue.classz(),
        from.get(criteriaValue.path()),
        criteriaBuilder.literal(getNormalizedJsonPath(criteriaValue.jsonPath())));
  }
}
