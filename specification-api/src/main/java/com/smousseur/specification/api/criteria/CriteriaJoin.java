package com.smousseur.specification.api.criteria;

public record CriteriaJoin(String path) implements Criteria {
  @Override
  public CriteriaType criteriaType() {
    return CriteriaType.JOIN;
  }
}
