package com.smousseur.specification.api.criteria;

public enum CriteriaJoinOption {
  NONE("none"),
  FETCH("fetch"),
  LEFT("left"),
  RIGHT("right");

  private final String value;

  CriteriaJoinOption(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
