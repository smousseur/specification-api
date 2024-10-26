package com.smousseur.specification.api.criteria;

/** The interface Abstract criteria. */
public interface Criteria {
  /**
   * Path string.
   *
   * @return the string
   */
  String path();

  CriteriaType criteriaType();
}
