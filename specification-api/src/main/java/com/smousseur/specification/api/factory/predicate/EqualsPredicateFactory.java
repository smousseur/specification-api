package com.smousseur.specification.api.factory.predicate;

import com.smousseur.specification.api.criteria.CriteriaValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public class EqualsPredicateFactory implements PredicateFactory {
  @Override
  public <Z, X> Predicate createPredicate(
      CriteriaValue criteriaValue,
      String sqlDialect,
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.equal(
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder),
        criteriaValue.value());
  }
}
