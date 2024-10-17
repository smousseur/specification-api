package com.smousseur.specification.api.service.internal.json;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgresJsonExtractor implements JsonExtractExtractor {
  private static final String JSON_EXTRACT = "jsonb_extract_path_text";

  @Override
  public <Z, X, T> Predicate buildJsonEqualPredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      Class<T> classz,
      Object value) {
    Expression<?>[] jsonParts = getJsonExpressionParts(from, criteriaBuilder, pathToJson, jsonPath);
    return criteriaBuilder.equal(
        criteriaBuilder.function(JSON_EXTRACT, String.class, jsonParts), value);
  }

  @Override
  public <Z, X> Predicate buildJsonLikePredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      String value) {
    Expression<?>[] jsonParts = getJsonExpressionParts(from, criteriaBuilder, pathToJson, jsonPath);
    return criteriaBuilder.like(
        criteriaBuilder.function(JSON_EXTRACT, String.class, jsonParts), getLikeExpression(value));
  }

  private static <Z, X> Expression<?>[] getJsonExpressionParts(
      From<Z, X> from, CriteriaBuilder criteriaBuilder, String pathToJson, String jsonPath) {
    List<Expression<String>> expressions = new ArrayList<>();
    expressions.add(from.get(pathToJson));
    expressions.addAll(Arrays.stream(jsonPath.split("\\.")).map(criteriaBuilder::literal).toList());

    return expressions.toArray(Expression[]::new);
  }
}
