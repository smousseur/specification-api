package com.smousseur.specification.api.json;

import com.smousseur.specification.api.criteria.CriteriaJsonColumnType;
import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgresJsonExpression<T> implements JsonExpression<T> {
  private static final String JSON_EXTRACT = "json_extract_path_text";
  private static final String JSONB_EXTRACT = "jsonb_extract_path_text";

  @Override
  public <Z, X> Expression<T> getExpression(
      CriteriaJsonValue<T> criteriaJsonValue, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Expression<?>[] jsonParts =
        getJsonExpressionParts(
            from, criteriaBuilder, criteriaJsonValue.path(), criteriaJsonValue.jsonPath());
    String jsonExtractFunction = getJsonExtractFunction(criteriaJsonValue.columnType());

    return criteriaBuilder.function(jsonExtractFunction, criteriaJsonValue.classz(), jsonParts);
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
