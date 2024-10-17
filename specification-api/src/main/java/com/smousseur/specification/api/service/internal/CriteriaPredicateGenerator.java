package com.smousseur.specification.api.service.internal;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import com.smousseur.specification.api.criteria.CriteriaValue;
import com.smousseur.specification.api.criteria.CriteriaValueOperation;
import com.smousseur.specification.api.criteria.CriteriaValueType;
import com.smousseur.specification.api.exception.SearchException;
import com.smousseur.specification.api.service.internal.json.DefaultJsonExtractor;
import com.smousseur.specification.api.service.internal.json.JsonExtractExtractor;
import com.smousseur.specification.api.service.internal.json.MySqlJsonExtractor;
import com.smousseur.specification.api.service.internal.json.PostgresJsonExtractor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

/**
 * The type Criteria predicate generator.
 *
 * @param <T> the type parameter
 */
public class CriteriaPredicateGenerator<T> {
  /** The Criteria value. */
  private final CriteriaValue<T> criteriaValue;

  /** The Sql dialect. */
  private final String sqlDialect;

  public CriteriaPredicateGenerator(CriteriaValue<T> criteriaValue, String sqlDialect) {
    this.criteriaValue = criteriaValue;
    this.sqlDialect = sqlDialect;
  }

  /**
   * Generate predicate predicate.
   *
   * @param <Z> the type parameter
   * @param <X> the type parameter
   * @param root the root
   * @param criteriaBuilder the criteria builder
   * @return the predicate
   */
  public <Z, X> Predicate generatePredicate(From<Z, X> root, CriteriaBuilder criteriaBuilder) {
    CriteriaValueOperation operation = criteriaValue.operation();
    return switch (operation) {
      case EQUALS -> buildEqualPredicate(root, criteriaBuilder);
      case LIKE -> buildLikePredicate(root, criteriaBuilder);
    };
  }

  private <Z, X> Predicate buildLikePredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    String path = criteriaValue.path();
    Object value = criteriaValue.value();
    CriteriaValueType type = criteriaValue.type();
    return switch (type) {
      case PROPERTY -> criteriaBuilder.like(from.get(path), "%" + value + "%");
      case JSON_PROPERTY -> {
        String jsonPath = ((CriteriaJsonValue<?>) criteriaValue).jsonPath();
        JsonExtractExtractor jsonExtractExtractor = getJsonExtractService();
        yield jsonExtractExtractor.buildJsonLikePredicate(
            from, criteriaBuilder, path, jsonPath, String.valueOf(value));
      }
    };
  }

  private <Z, X> Predicate buildEqualPredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    String path = criteriaValue.path();
    Object value = criteriaValue.value();
    CriteriaValueType type = criteriaValue.type();
    return switch (type) {
      case PROPERTY -> criteriaBuilder.equal(from.get(path), value);
      case JSON_PROPERTY -> {
        Class<T> classz = criteriaValue.classz();
        String jsonPath = ((CriteriaJsonValue<?>) criteriaValue).jsonPath();
        JsonExtractExtractor jsonExtractExtractor = getJsonExtractService();
        yield jsonExtractExtractor.buildJsonEqualPredicate(
            from, criteriaBuilder, path, jsonPath, classz, value);
      }
    };
  }

  private JsonExtractExtractor getJsonExtractService() {
    return switch (sqlDialect) {
      case "MySQL", "MariaDB" -> new MySqlJsonExtractor();
      case "PostgreSQL" -> new PostgresJsonExtractor();
      case "Oracle", "Microsoft SQL Server" -> new DefaultJsonExtractor();
      default -> throw new SearchException(sqlDialect + " is not supported to extract json fields");
    };
  }
}
