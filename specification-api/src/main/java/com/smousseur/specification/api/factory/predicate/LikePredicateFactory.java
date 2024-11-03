package com.smousseur.specification.api.factory.predicate;

import com.smousseur.specification.api.criteria.CriteriaValue;
import com.smousseur.specification.api.util.Utils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

public class LikePredicateFactory implements PredicateFactory {
  @Override
  public <Z, X> Predicate createPredicate(
      CriteriaValue criteriaValue,
      String sqlDialect,
      From<Z, X> from,
      CriteriaBuilder criteriaBuilder) {
    String value = String.valueOf(criteriaValue.value());
    final String likeValue = value.contains("%") ? value : Utils.wrap(value, "%");
    Expression<String> predicateExpression =
        criteriaValue.getPredicateExpression(sqlDialect, from, criteriaBuilder);
    return criteriaBuilder.like(predicateExpression, likeValue);
  }
}
