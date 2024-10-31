package com.smousseur.specification.api.criteria;

/** The interface Abstract criteria. */
public interface Criteria {
  /**
   * Path string.
   *
   * @return the string
   */
  String path();

  /**
   * Criteria type criteria type.
   *
   * @return the criteria type
   */
  CriteriaType criteriaType();
}
