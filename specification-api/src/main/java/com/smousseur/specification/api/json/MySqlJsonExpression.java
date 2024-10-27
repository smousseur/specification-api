package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

public class MySqlJsonExpression implements JsonExpression {
  private static final String JSON_EXTRACT = "json_extract";
  private static final String JSON_UNQUOTE = "json_unquote";

  @Override
  public <Z, X> Expression<?> getExpression(
      CriteriaJsonValue criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Class<?> valueClass = criteriaJsonValue.value().getClass();
    Expression<?> jsonExtract =
        criteriaBuilder.function(
            JSON_EXTRACT,
            valueClass,
            from.get(criteriaJsonValue.path()),
            criteriaBuilder.literal(getNormalizedJsonPath(criteriaJsonValue.jsonPath())));
    return criteriaBuilder.function(JSON_UNQUOTE, valueClass, jsonExtract);
  }
}
