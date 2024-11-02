package com.smousseur.specification.api.criteria;

public enum CriteriaJoinOption {
  NONE("none"),
  FETCH("fetch"),
  LEFT("left");

  private final String value;

  CriteriaJoinOption(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
