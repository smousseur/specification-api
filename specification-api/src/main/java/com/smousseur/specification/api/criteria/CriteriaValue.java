package com.smousseur.specification.api.criteria;

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
  CriteriaValueOperation operation();

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
}
