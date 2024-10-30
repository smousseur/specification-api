package com.smousseur.specification.api.service;

import com.smousseur.specification.api.annotation.PredicateDef;
import com.smousseur.specification.api.annotation.SpecificationDef;
import com.smousseur.specification.api.antlr.CriteriaLexer;
import com.smousseur.specification.api.antlr.CriteriaParser;
import com.smousseur.specification.api.exception.SpecificationException;
import com.smousseur.specification.api.exception.SpecificationParseException;
import jakarta.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Arrays;
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
public abstract class SpecificationValidationService {
  /** The Resource loader. */
  private final ResourceLoader resourceLoader;

  protected SpecificationValidationService(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  /** Validate. */
  @PostConstruct
  public void validate() {
    Arrays.stream(packagesToScan()).forEach(this::validatePackage);
  }

  public abstract String[] packagesToScan();

  private void validatePackage(String packageToValidate) {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    TypeFilter annotationFilter = new AnnotationTypeFilter(SpecificationDef.class);
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
                throw new SpecificationException("Cannot get class: " + className, e);
              }
            });
    searchClasses.forEach(this::validateClass);
  }

  private void validateClass(Class<?> searchClass) {
    ReflectionUtils.doWithFields(
        searchClass,
        field -> validateField(searchClass, field),
        field -> AnnotationUtils.getAnnotation(field, PredicateDef.class) != null);
  }

  private void validateField(Class<?> searchClass, Field field) {
    PredicateDef annotationSearch =
        Optional.ofNullable(AnnotationUtils.getAnnotation(field, PredicateDef.class)).orElseThrow();
    CharStream charStream = CharStreams.fromString(annotationSearch.value());
    CriteriaLexer lexer = new CriteriaLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CriteriaParser parser = new CriteriaParser(tokens);
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
            throw new SpecificationParseException(
                "Invalid search expression for class %s and field \"%s\", message: %s",
                searchClass.getName(), field.getName(), msg);
          }
        });
    parser.criteria();
  }
}
