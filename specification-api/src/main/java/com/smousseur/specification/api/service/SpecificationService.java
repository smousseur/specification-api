package com.smousseur.specification.api.service;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.criteria.Criteria;
import com.smousseur.specification.api.exception.SpecificationException;
import com.smousseur.specification.api.generator.CriteriaSpecificationGenerator;
import com.smousseur.specification.api.generator.SpecificationParser;
import com.smousseur.specification.api.util.Utils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;

/** The type Specification service. */
public class SpecificationService {
  private final String sqlDialect;

  public SpecificationService(JdbcTemplate jdbcTemplate) {
    this.sqlDialect =
        jdbcTemplate.execute(
            (ConnectionCallback<String>)
                connection -> connection.getMetaData().getDatabaseProductName());
  }

  /**
   * Generate specification specification.
   *
   * @param <T> the type parameter
   * @param <R> the type parameter
   * @param searchRequest the search request
   * @return the specification
   */
  public <T, R> Specification<T> generateSpecification(R searchRequest) {
    Class<?> searchRequestClass = searchRequest.getClass();
    List<Pair<String, Object>> requestSpecs = new ArrayList<>();
    ReflectionUtils.doWithFields(
        searchRequestClass,
        field -> processField(field, searchRequest, requestSpecs),
        SpecificationService::filterFieldsWithSearchPath);
    return generateSpecification(requestSpecs);
  }

  private <T> Specification<T> generateSpecification(List<Pair<String, Object>> requestSpecs) {
    List<Criteria> criterias =
        requestSpecs.stream()
            .map(spec -> new SpecificationParser(spec.getFirst(), spec.getSecond()).parse())
            .flatMap(List::stream)
            .toList();
    return new CriteriaSpecificationGenerator<T>(criterias, this.sqlDialect)
        .generateSpecification();
  }

  private static <R> void processField(
      Field field, R searchRequest, List<Pair<String, Object>> requestSpecs) {
    Optional.ofNullable(Utils.callGetterForField(field, searchRequest))
        .map(value -> mapPath(field, value))
        .ifPresent(requestSpecs::add);
  }

  private static Pair<String, Object> mapPath(Field field, Object value) {
    return Optional.ofNullable(AnnotationUtils.getAnnotation(field, SearchPath.class))
        .map(pathAnnotation -> Pair.of(pathAnnotation.value(), value))
        .orElseThrow(() -> new SpecificationException("Cannot get search annotation"));
  }

  private static boolean filterFieldsWithSearchPath(Field field) {
    return AnnotationUtils.getAnnotation(field, SearchPath.class) != null;
  }
}
