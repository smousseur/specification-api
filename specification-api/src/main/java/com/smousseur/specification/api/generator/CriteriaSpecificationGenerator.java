package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.criteria.Criteria;
import com.smousseur.specification.api.criteria.CriteriaJoin;
import com.smousseur.specification.api.criteria.CriteriaType;
import com.smousseur.specification.api.criteria.CriteriaValue;
import com.smousseur.specification.api.parser.SpecificationExpressionParser;
import jakarta.persistence.criteria.*;
import java.util.*;
import org.springframework.data.jpa.domain.Specification;

/**
 * The type Criteria specification generator.
 *
 * @param <T> the type parameter
 */
public class CriteriaSpecificationGenerator<T> {
  /** The Criterias. */
  private final List<Criteria> criterias;

  /** The Sql dialect. */
  private final String sqlDialect;

  private final String specificationExp;

  private final Map<String, Join<Object, Object>> joins = new HashMap<>();

  public CriteriaSpecificationGenerator(
      List<Criteria> criterias, String specificationExp, String sqlDialect) {
    this.criterias = criterias;
    this.sqlDialect = sqlDialect;
    this.specificationExp = specificationExp;
  }

  /**
   * Generate specification specification.
   *
   * @return the specification
   */
  public Specification<T> generateSpecification() {
    Map<String, Predicate> predicateMap = new HashMap<>();
    return (root, query, criteriaBuilder) -> {
      Join<Object, Object> currentJoin = null;
      for (Criteria criteria : criterias) {
        if (criteria.criteriaType() == CriteriaType.JOIN) {
          CriteriaJoin node = (CriteriaJoin) criteria;
          currentJoin = getCurrentJoin(root, currentJoin, node);
        } else if (criteria.criteriaType() == CriteriaType.VALUE) {
          CriteriaValue value = (CriteriaValue) criteria;
          CriteriaPredicateGenerator criteriaPredicateGenerator =
              new CriteriaPredicateGenerator(value, sqlDialect);
          From<?, ?> from = currentJoin != null ? currentJoin : root;
          Predicate predicate = criteriaPredicateGenerator.generatePredicate(from, criteriaBuilder);
          predicateMap.put(value.id(), predicate);
          currentJoin = null;
        }
      }
      return computePredicate(criteriaBuilder, predicateMap);
    };
  }

  private Join<Object, Object> getCurrentJoin(
      Root<T> root, Join<Object, Object> currentJoin, CriteriaJoin node) {
    Join<Object, Object> join;
    join = joins.get(node.path());
    if (join != null) {
      currentJoin = join;
    } else {
      currentJoin = currentJoin == null ? root.join(node.path()) : currentJoin.join(node.path());
      joins.put(node.path(), currentJoin);
    }
    return currentJoin;
  }

  private Predicate computePredicate(
      CriteriaBuilder criteriaBuilder, Map<String, Predicate> predicateMap) {
    Predicate result;
    if (specificationExp.isBlank()) {
      result = criteriaBuilder.and(predicateMap.values().toArray(new Predicate[0]));
    } else {
      SpecificationExpressionParser parser =
          new SpecificationExpressionParser(specificationExp, criteriaBuilder, predicateMap);
      result = parser.parse();
    }

    return result;
  }
}
