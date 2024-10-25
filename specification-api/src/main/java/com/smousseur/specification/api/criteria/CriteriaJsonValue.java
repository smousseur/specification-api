package com.smousseur.specification.api.criteria;

import com.smousseur.specification.api.exception.SpecificationException;
import com.smousseur.specification.api.json.DefaultJsonExpression;
import com.smousseur.specification.api.json.JsonExpression;
import com.smousseur.specification.api.json.MySqlJsonExpression;
import com.smousseur.specification.api.json.PostgresJsonExpression;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

/**
 * The type Criteria json value.
 *
 * @param <T> the type parameter
 */
public record CriteriaJsonValue<T>(
    String path,
    String jsonPath,
    CriteriaJsonColumnType columnType,
    CriteriaOperation operation,
    CriteriaValueType type,
    T value,
    Class<T> classz)
    implements CriteriaValue<T> {
  public <Z, X> Expression<T> getPredicateExpression(
      String sqlDialect, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    JsonExpression<T> jsonExtractService = getJsonExtractService(sqlDialect);
    return jsonExtractService.getEvaluatedExpression(this, from, criteriaBuilder);
  }

  private JsonExpression<T> getJsonExtractService(String sqlDialect) {
    return switch (sqlDialect) {
      case "MySQL", "MariaDB" -> new MySqlJsonExpression<>();
      case "PostgreSQL" -> new PostgresJsonExpression<>();
      case "Oracle", "Microsoft SQL Server" -> new DefaultJsonExpression<>();
      default ->
          throw new SpecificationException(sqlDialect + " is not supported to extract json fields");
    };
  }
}
