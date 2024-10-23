package com.smousseur.specification.api.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

public record CriteriaObjectValue<T>(
    String path, CriteriaValueOperation operation, CriteriaValueType type, T value, Class<T> classz)
    implements CriteriaValue<T> {
  @Override
  public <Z, X> Expression<T> getPredicateExpression(
      String sqlDialect, From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    return from.get(path);
  }
}
