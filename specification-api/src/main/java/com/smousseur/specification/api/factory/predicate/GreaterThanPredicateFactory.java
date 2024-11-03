package com.smousseur.specification.api.factory.predicate;

import com.smousseur.specification.api.criteria.CriteriaValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public class GreaterThanPredicateFactory implements PredicateFactory {
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <Z, X> Predicate createPredicate(
      CriteriaValue criteriaValue,
      String sqlDialect,
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder) {
    Comparable<? super Comparable> value = (Comparable) criteriaValue.value();
    Expression<? extends Comparable> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.greaterThan(predicateExpression, value);
  }
}
