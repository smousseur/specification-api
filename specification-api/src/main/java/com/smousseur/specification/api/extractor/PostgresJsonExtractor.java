package com.smousseur.specification.api.extractor;

import com.smousseur.specification.api.criteria.CriteriaJsonColumnType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgresJsonExtractor implements JsonExtractExtractor {
  private static final String JSON_EXTRACT = "json_extract_path_text";
  private static final String JSONB_EXTRACT = "jsonb_extract_path_text";

  @Override
  public <Z, X, T> Predicate buildJsonEqualPredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      CriteriaJsonColumnType columnType,
      Class<T> classz,
      Object value) {
    Expression<?>[] jsonParts = getJsonExpressionParts(from, criteriaBuilder, pathToJson, jsonPath);
    String jsonExtractFunction = getJsonExtractFunction(columnType);
    return criteriaBuilder.equal(
        criteriaBuilder.function(jsonExtractFunction, classz, jsonParts), value);
  }

  @Override
  public <Z, X> Predicate buildJsonLikePredicate(
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder,
      String pathToJson,
      String jsonPath,
      CriteriaJsonColumnType columnType,
      String value) {
    Expression<?>[] jsonParts = getJsonExpressionParts(from, criteriaBuilder, pathToJson, jsonPath);
    String jsonExtractFunction = getJsonExtractFunction(columnType);
    return criteriaBuilder.like(
        criteriaBuilder.function(jsonExtractFunction, String.class, jsonParts),
        getLikeExpression(value));
  }

  private static <Z, X> Expression<?>[] getJsonExpressionParts(
      From<Z, X> from, CriteriaBuilder criteriaBuilder, String pathToJson, String jsonPath) {
    List<Expression<String>> expressions = new ArrayList<>();
    expressions.add(from.get(pathToJson));
    expressions.addAll(Arrays.stream(jsonPath.split("\\.")).map(criteriaBuilder::literal).toList());

    return expressions.toArray(Expression[]::new);
  }

  private static String getJsonExtractFunction(CriteriaJsonColumnType columnType) {
    return switch (columnType) {
      case JSON -> JSON_EXTRACT;
      case JSONB -> JSONB_EXTRACT;
    };
  }
}
