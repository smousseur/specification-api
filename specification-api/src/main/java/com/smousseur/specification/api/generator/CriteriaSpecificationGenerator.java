package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.criteria.CriteriaJoin;
import com.smousseur.specification.api.criteria.CriteriaType;
import com.smousseur.specification.api.criteria.CriteriaValue;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * The type Criteria specification generator.
 *
 * @param <T> the type parameter
 */
public class CriteriaSpecificationGenerator<T> {
  /** The Criterias. */
  private final List<AbstractCriteria> criterias;

  /** The Sql dialect. */
  private final String sqlDialect;

  public CriteriaSpecificationGenerator(List<AbstractCriteria> criterias, String sqlDialect) {
    this.criterias = criterias;
    this.sqlDialect = sqlDialect;
  }

  /**
   * Generate specification specification.
   *
   * @return the specification
   */
  public Specification<T> generateSpecification() {
    Specification<T> specification = Specification.where(null);
    List<Specification<T>> specs = new ArrayList<>();
    specs.add(
        (root, query, criteriaBuilder) -> {
          Join<Object, Object> currentJoin = null;
          List<Predicate> predicates = new ArrayList<>();
          for (AbstractCriteria criteria : criterias) {
            if (criteria.criteriaType() == CriteriaType.JOIN) {
              CriteriaJoin node = (CriteriaJoin) criteria;
              currentJoin =
                  currentJoin == null ? root.join(node.path()) : currentJoin.join(node.path());
            } else if (criteria.criteriaType() == CriteriaType.VALUE) {
              CriteriaValue<?> value = (CriteriaValue<?>) criteria;
              CriteriaPredicateGenerator<?> criteriaPredicateGenerator =
                  new CriteriaPredicateGenerator<>(value, sqlDialect);
              From<?, ?> from = currentJoin != null ? currentJoin : root;
              predicates.add(criteriaPredicateGenerator.generatePredicate(from, criteriaBuilder));
              currentJoin = null;
            }
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    specification = getSpecification(specification, specs);
    return specification;
  }

  private Specification<T> getSpecification(
      Specification<T> specification, List<Specification<T>> specs) {
    for (Specification<T> spec : specs) {
      specification = specification.and(spec);
    }
    return specification;
  }
}
