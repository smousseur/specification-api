package com.smousseur.specification.api.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

public record CriteriaObjectValue(
    String id, String path, CriteriaOperation operation, CriteriaValueType type, Object value)
    implements CriteriaValue {
  @Override
  public <T, Z, X> Expression<T> getPredicateExpression(
      String sqlDialect, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    return from.get(path);
  }
}
