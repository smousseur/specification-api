package com.smousseur.specification.api.service;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.service.config.SearchServiceConfiguration;
import com.smousseur.specification.api.service.internal.CriteriaSpecificationGenerator;
import com.smousseur.specification.api.service.internal.SpecificationParser;
import com.smousseur.specification.api.util.Utils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;

/** The type Specification service. */
public class SpecificationService {
  private final SearchServiceConfiguration configuration;

  public SpecificationService(SearchServiceConfiguration configuration) {
    this.configuration = configuration;
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
    List<String> requestSpecs = new ArrayList<>();
    ReflectionUtils.doWithFields(
        searchRequestClass,
        field -> processField(field, searchRequest, requestSpecs),
        SpecificationService::filterFieldsWithSearchPath);
    return generateSpecification(requestSpecs);
  }

  private <T> Specification<T> generateSpecification(List<String> requestSpecs) {
    List<AbstractCriteria> criterias =
        requestSpecs.stream()
            .map(spec -> new SpecificationParser(spec).parse())
            .flatMap(List::stream)
            .toList();
    return new CriteriaSpecificationGenerator<T>(criterias, configuration.getSqlDialect())
        .generateSpecification();
  }

  private static <R> void processField(Field field, R searchRequest, List<String> requestSpecs) {
    Optional.ofNullable(Utils.callFieldGetter(field, searchRequest))
        .map(value -> mapPath(field, value))
        .ifPresent(requestSpecs::add);
  }

  private static String mapPath(Field field, Object value) {
    String annotationPath =
        Optional.ofNullable(AnnotationUtils.getAnnotation(field, SearchPath.class))
            .map(SearchPath::value)
            .orElseThrow();
    return String.format(annotationPath, value);
  }

  private static boolean filterFieldsWithSearchPath(Field field) {
    return AnnotationUtils.getAnnotation(field, SearchPath.class) != null;
  }
}
