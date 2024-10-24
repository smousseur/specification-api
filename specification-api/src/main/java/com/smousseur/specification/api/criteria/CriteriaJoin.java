package com.smousseur.specification.api.criteria;

public record CriteriaJoin(String path) implements AbstractCriteria {
  @Override
  public CriteriaType criteriaType() {
    return CriteriaType.JOIN;
  }
}
