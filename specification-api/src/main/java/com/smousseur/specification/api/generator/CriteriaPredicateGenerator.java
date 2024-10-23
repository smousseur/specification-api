package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import com.smousseur.specification.api.criteria.CriteriaValue;
import com.smousseur.specification.api.criteria.CriteriaValueOperation;
import com.smousseur.specification.api.criteria.CriteriaValueType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
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
   * @param from the from
   * @param criteriaBuilder the criteria builder
   * @return the predicate
   */
  public <Z, X> Predicate generatePredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    CriteriaValueOperation operation = criteriaValue.operation();
    return switch (operation) {
      case EQUALS -> buildEqualPredicate(from, criteriaBuilder);
      case LIKE -> buildLikePredicate(from, criteriaBuilder);
      case GREATER_THAN -> buildGreaterThanPredicate(from, criteriaBuilder);
      case LESS_THAN -> buildLessThanPredicate(from, criteriaBuilder);
      case GREATER_THAN_OR_EQUALS -> buildGreaterThanOrEqualsPredicate(from, criteriaBuilder);
      case LESS_THAN_OR_EQUALS -> buildLessThanOrEqualsPredicate(from, criteriaBuilder);
    };
  }

  private <Z, X> Predicate buildLessThanOrEqualsPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        (Expression) criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.lessThanOrEqualTo(predicateExpression, value);
  }

  private <Z, X> Predicate buildGreaterThanOrEqualsPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        (Expression) criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.greaterThanOrEqualTo(predicateExpression, value);
  }

  private <Z, X> Predicate buildLessThanPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        (Expression) criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.lessThan(predicateExpression, value);
  }

  private <Z, X> Predicate buildGreaterThanPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        (Expression<? extends Comparable>)
            criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.greaterThan(predicateExpression, value);
  }

  private <Z, X> Predicate buildLikePredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    String path = criteriaValue.path();
    String value = String.valueOf(criteriaValue.value());
    CriteriaValueType type = criteriaValue.type();
    return switch (type) {
      case PROPERTY -> criteriaBuilder.like(from.get(path), "%" + value + "%");
      case JSON_PROPERTY -> {
        CriteriaJsonValue<String> criteriaJsonValue = (CriteriaJsonValue<String>) criteriaValue;
        yield criteriaBuilder.like(
            criteriaJsonValue.getPredicateExpression(sqlDialect, from, criteriaBuilder),
            getLikeExpression(value));
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
        CriteriaJsonValue<?> criteriaJsonValue = (CriteriaJsonValue<?>) criteriaValue;
        yield criteriaBuilder.equal(
            criteriaJsonValue.getPredicateExpression(sqlDialect, from, criteriaBuilder), value);
      }
    };
  }

  private static String getLikeExpression(String value) {
    return "%" + value + "%";
  }
}
