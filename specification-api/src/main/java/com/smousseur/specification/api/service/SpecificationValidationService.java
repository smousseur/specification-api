package com.smousseur.specification.api.service;

import com.smousseur.specification.api.annotation.SearchPath;
import com.smousseur.specification.api.annotation.SearchRequestObject;
import com.smousseur.specification.api.antlr.ExpressionLexer;
import com.smousseur.specification.api.antlr.ExpressionParser;
import com.smousseur.specification.api.exception.ParseException;
import com.smousseur.specification.api.exception.SearchException;
import com.smousseur.specification.api.service.config.SearchServiceConfiguration;
import jakarta.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.antlr.v4.runtime.*;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ReflectionUtils;

/** The type Specification validation service. */
public class SpecificationValidationService {
  /** The Resource loader. */
  private final ResourceLoader resourceLoader;

  /** The Configuration. */
  private final SearchServiceConfiguration configuration;

  public SpecificationValidationService(
      SearchServiceConfiguration configuration, ResourceLoader resourceLoader) {
    this.configuration = configuration;
    this.resourceLoader = resourceLoader;
  }

  /** Validate. */
  @PostConstruct
  public void validate() {
    String[] packages = configuration.packagesToScan();
    for (String package_ : packages) {
      validatePackage(package_);
    }
  }

  private void validatePackage(String packageToValidate) {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    TypeFilter annotationFilter = new AnnotationTypeFilter(SearchRequestObject.class);
    scanner.addIncludeFilter(annotationFilter);
    scanner.setResourceLoader(new PathMatchingResourcePatternResolver(resourceLoader));
    Set<Class<?>> searchClasses = new HashSet<>();
    scanner
        .findCandidateComponents(packageToValidate)
        .forEach(
            beanDefinition -> {
              String className = null;
              try {
                className = beanDefinition.getBeanClassName();
                searchClasses.add(Class.forName(className));
              } catch (ClassNotFoundException e) {
                throw new SearchException("Cannot get class: " + className, e);
              }
            });
    searchClasses.forEach(this::validateClass);
  }

  private void validateClass(Class<?> searchClass) {
    ReflectionUtils.doWithFields(
        searchClass,
        field -> validateField(searchClass, field),
        field -> AnnotationUtils.getAnnotation(field, SearchPath.class) != null);
  }

  private void validateField(Class<?> searchClass, Field field) {
    SearchPath annotationSearch =
        Optional.ofNullable(AnnotationUtils.getAnnotation(field, SearchPath.class)).orElseThrow();
    String expression = annotationSearch.value().replace("%", "");
    CharStream charStream = CharStreams.fromString(expression);
    ExpressionLexer lexer = new ExpressionLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ExpressionParser parser = new ExpressionParser(tokens);
    parser.addErrorListener(
        new BaseErrorListener() {
          @Override
          public void syntaxError(
              Recognizer<?, ?> recognizer,
              Object offendingSymbol,
              int line,
              int charPositionInLine,
              String msg,
              RecognitionException e) {
            throw new ParseException(
                "Invalid search expression for class %s and field \"%s\", message: %s",
                searchClass.getName(), field.getName(), msg);
          }
        });
    parser.expression();
  }
}
