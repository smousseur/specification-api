package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.criteria.CriteriaOperation;
import com.smousseur.specification.api.criteria.CriteriaValue;
import com.smousseur.specification.api.util.Utils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.List;

/** The type Criteria predicate generator. */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CriteriaPredicateGenerator {
  /** The Criteria value. */
  private final CriteriaValue criteriaValue;

  /** The Sql dialect. */
  private final String sqlDialect;

  public CriteriaPredicateGenerator(CriteriaValue criteriaValue, String sqlDialect) {
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
    CriteriaOperation operation = criteriaValue.operation();
    return switch (operation) {
      case EQUALS -> buildEqualPredicate(from, criteriaBuilder);
      case LIKE -> buildLikePredicate(from, criteriaBuilder);
      case GREATER_THAN -> buildGreaterThanPredicate(from, criteriaBuilder);
      case LESS_THAN -> buildLessThanPredicate(from, criteriaBuilder);
      case GREATER_THAN_OR_EQUALS -> buildGreaterThanOrEqualsPredicate(from, criteriaBuilder);
      case LESS_THAN_OR_EQUALS -> buildLessThanOrEqualsPredicate(from, criteriaBuilder);
      case CONTAINS -> buildContainsPredicate(from, criteriaBuilder);
      case IN -> buildInPredicate(from, criteriaBuilder);
    };
  }

  private <Z, X> Predicate buildInPredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    return criteriaValue
        .getPredicateExpression(sqlDialect, from, criteriaBuilder)
        .in(((List<?>) criteriaValue.value()).toArray(new Object[0]));
  }

  private <Z, X> Predicate buildContainsPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Expression<?> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.isMember(
        criteriaValue.value(), (Expression<? extends Collection<Object>>) predicateExpression);
  }

  private <Z, X> Predicate buildLessThanOrEqualsPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.lessThanOrEqualTo(predicateExpression, value);
  }

  private <Z, X> Predicate buildGreaterThanOrEqualsPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.greaterThanOrEqualTo(predicateExpression, value);
  }

  private <Z, X> Predicate buildLessThanPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.lessThan(predicateExpression, value);
  }

  private <Z, X> Predicate buildGreaterThanPredicate(
      From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.greaterThan(predicateExpression, value);
  }

  private <Z, X> Predicate buildLikePredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    String value = String.valueOf(criteriaValue.value());
    final String likeValue = Utils.wrap(value, "%");
    Expression<String> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.like(predicateExpression, likeValue);
  }

  private <Z, X> Predicate buildEqualPredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.equal(
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder),
        criteriaValue.value());
  }
}
