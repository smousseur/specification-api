package com.smousseur.specification.api.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

/**
 * The interface Criteria value.
 *
 * @param <T> the type parameter
 */
public interface CriteriaValue<T> extends AbstractCriteria {
  String path();

  /**
   * Operation criteria value operation.
   *
   * @return the criteria value operation
   */
  CriteriaOperation operation();

  /**
   * Type criteria value type.
   *
   * @return the criteria value type
   */
  CriteriaValueType type();

  /**
   * Value t.
   *
   * @return the t
   */
  T value();

  /**
   * Classz class.
   *
   * @return the class
   */
  Class<T> classz();

  <Z, X> Expression<T> getPredicateExpression(
      String sqlDialect, From<Z, X> from, CriteriaBuilder criteriaBuilder);

  @Override
  default CriteriaType criteriaType() {
    return CriteriaType.VALUE;
  }
}
