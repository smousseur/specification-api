package com.smousseur.specification.api.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

/** The interface Criteria value. */
public interface CriteriaValue extends Criteria {
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
  Object value();

  <T, Z, X> Expression<T> getPredicateExpression(
      String sqlDialect, From<Z, X> from, CriteriaBuilder criteriaBuilder);

  @Override
  default CriteriaType criteriaType() {
    return CriteriaType.VALUE;
  }
}
