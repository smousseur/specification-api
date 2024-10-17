package com.smousseur.specification.api.service.internal.json;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public class DefaultJsonExtractor implements JsonExtractExtractor {
  private static final String JSON_VALUE = "json_value";

  @Override
  public <Z, X, T> Predicate buildJsonEqualPredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      Class<T> classz,
      Object value) {
    return criteriaBuilder.equal(
        criteriaBuilder.function(
            JSON_VALUE,
            classz,
            from.get(pathToJson),
            criteriaBuilder.literal(getNormalizedJsonPath(jsonPath))),
        value);
  }

  @Override
  public <Z, X> Predicate buildJsonLikePredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      String value) {
    return criteriaBuilder.like(
        criteriaBuilder.function(
            JSON_VALUE,
            String.class,
            from.get(pathToJson),
            criteriaBuilder.literal(getNormalizedJsonPath(jsonPath))),
        getLikeExpression(value));
  }
}
