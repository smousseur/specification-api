package com.smousseur.specification.api.service.internal;

import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.criteria.CriteriaNode;
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
          Join<Object, Object> join = null;
          List<Predicate> predicates = new ArrayList<>();
          for (AbstractCriteria criteria : criterias) {
            if (criteria instanceof CriteriaNode node) {
              join = join == null ? root.join(node.path()) : join.join(node.path());
            } else if (criteria instanceof CriteriaValue<?> value) {
              com.smousseur.specification.api.service.internal.CriteriaPredicateGenerator<?>
                  criteriaPredicateGenerator = new CriteriaPredicateGenerator<>(value, sqlDialect);
              Predicate predicate;
              if (join != null) {
                predicate = criteriaPredicateGenerator.generatePredicate(join, criteriaBuilder);
                join = null;
              } else {
                predicate = criteriaPredicateGenerator.generatePredicate(root, criteriaBuilder);
              }
              predicates.add(predicate);
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
