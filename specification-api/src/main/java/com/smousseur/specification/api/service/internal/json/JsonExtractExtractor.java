package com.smousseur.specification.api.service.internal.json;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public interface JsonExtractExtractor {
  <Z, X, T> Predicate buildJsonEqualPredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      Class<T> classz,
      Object value);

  <Z, X> Predicate buildJsonLikePredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      String value);

  default String getLikeExpression(String value) {
    return "%" + value + "%";
  }

  default String getNormalizedJsonPath(String jsonPath) {
    return "$." + jsonPath;
  }
}
