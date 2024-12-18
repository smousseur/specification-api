package com.smousseur.specification.api.criteria;

import com.smousseur.specification.api.exception.SpecificationProcessingException;
import com.smousseur.specification.api.json.DefaultJsonExpression;
import com.smousseur.specification.api.json.JsonExpression;
import com.smousseur.specification.api.json.MySqlJsonExpression;
import com.smousseur.specification.api.json.PostgresJsonExpression;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

/** The type Criteria json value. */
public record CriteriaJsonValue(
    String id,
    String path,
    String jsonPath,
    CriteriaJsonColumnType columnType,
    CriteriaOperation operation,
    CriteriaValueType type,
    Object value)
    implements CriteriaValue {
  public <T, Z, X> Expression<T> getPredicateExpression(
      String sqlDialect, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    JsonExpression jsonExtractService = getJsonExtractService(sqlDialect);
    return jsonExtractService.getEvaluatedExpression(this, from, criteriaBuilder);
  }

  private JsonExpression getJsonExtractService(String sqlDialect) {
    return switch (sqlDialect) {
      case "MySQL", "MariaDB" -> new MySqlJsonExpression();
      case "PostgreSQL" -> new PostgresJsonExpression();
      case "Oracle", "Microsoft SQL Server" -> new DefaultJsonExpression();
      default ->
          throw new SpecificationProcessingException(
              sqlDialect + " is not supported to extract json fields");
    };
  }
}
