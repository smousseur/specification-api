package com.smousseur.specification.api.factory;

import com.smousseur.specification.api.criteria.CriteriaOperation;
import com.smousseur.specification.api.criteria.CriteriaValue;
import com.smousseur.specification.api.exception.SpecificationProcessingException;
import com.smousseur.specification.api.factory.predicate.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.*;

/** The type Criteria predicate factory. */
public class CriteriaPredicateFactory {
  /** The Criteria value. */
  private final CriteriaValue criteriaValue;

  /** The Sql dialect. */
  private final String sqlDialect;

  private static final EnumMap<CriteriaOperation, PredicateFactory> predicateFactories =
      new EnumMap<>(CriteriaOperation.class);

  static {
    predicateFactories.put(CriteriaOperation.EQUALS, new EqualsPredicateFactory());
    predicateFactories.put(CriteriaOperation.NOT_EQUALS, new NotEqualsPredicateFactory());
    predicateFactories.put(CriteriaOperation.LIKE, new LikePredicateFactory());
    predicateFactories.put(CriteriaOperation.GREATER_THAN, new GreaterThanPredicateFactory());
    predicateFactories.put(CriteriaOperation.LESS_THAN, new LessThanPredicateFactory());
    predicateFactories.put(
        CriteriaOperation.GREATER_THAN_OR_EQUALS, new GreaterThanOrEqualPredicateFactory());
    predicateFactories.put(
        CriteriaOperation.LESS_THAN_OR_EQUALS, new LessThanOrEqualPredicateFactory());
    predicateFactories.put(CriteriaOperation.CONTAINS, new ContainsPredicateFactory());
    predicateFactories.put(CriteriaOperation.IN, new InPredicateFactory());
    predicateFactories.put(CriteriaOperation.ISNULL, new IsNullPredicateFactory());
    predicateFactories.put(CriteriaOperation.ISNOTNULL, new IsNotNullPredicateFactory());
  }

  public CriteriaPredicateFactory(CriteriaValue criteriaValue, String sqlDialect) {
    this.criteriaValue = criteriaValue;
    this.sqlDialect = sqlDialect;
  }

  /**
   * Create predicate predicate.
   *
   * @param <Z> the type parameter
   * @param <X> the type parameter
   * @param from the from
   * @param criteriaBuilder the criteria builder
   * @return the predicate
   */
  public <Z, X> Predicate createPredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder) {
    CriteriaOperation operation = criteriaValue.operation();
    return Optional.ofNullable(predicateFactories.get(operation))
        .map(factory -> factory.createPredicate(criteriaValue, sqlDialect, from, criteriaBuilder))
        .orElseThrow(
            () ->
                new SpecificationProcessingException(
                    "Cannot create predicate with operation: " + operation.name()));
  }
}
