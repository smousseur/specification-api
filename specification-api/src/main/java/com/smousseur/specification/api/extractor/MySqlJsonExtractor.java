package com.smousseur.specification.api.extractor;

import com.smousseur.specification.api.criteria.CriteriaJsonColumnType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public class MySqlJsonExtractor implements JsonExtractExtractor {
  private static final String JSON_EXTRACT = "json_extract";
  private static final String JSON_UNQUOTE = "json_unquote";

  @Override
  public <Z, X, T> Predicate buildJsonEqualPredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      CriteriaJsonColumnType columnType,
      Class<T> classz,
      Object value) {
    Expression<T> jsonExtract =
        criteriaBuilder.function(
            JSON_EXTRACT,
            classz,
            from.get(pathToJson),
            criteriaBuilder.literal(getNormalizedJsonPath(jsonPath)));
    return criteriaBuilder.equal(
        criteriaBuilder.function(JSON_UNQUOTE, classz, jsonExtract), value);
  }

  @Override
  public <Z, X> Predicate buildJsonLikePredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      CriteriaJsonColumnType columnType,
      String value) {
    Expression<String> jsonExtract =
        criteriaBuilder.function(
            JSON_EXTRACT,
            String.class,
            from.get(pathToJson),
            criteriaBuilder.literal(getNormalizedJsonPath(jsonPath)));
    return criteriaBuilder.like(
        criteriaBuilder.function(JSON_UNQUOTE, String.class, jsonExtract),
        getLikeExpression(value));
  }
}
