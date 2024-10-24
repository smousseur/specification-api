# Specification Api

## Introduction

This is a simple library to handle generic search using Criteria/Specification API with JPA.

It generates a JPA `Specification<T>` with all criterias given a request object.

## Entry point

`SpecificationService` is the entry point of the specification generator. The generated `Specification<T>`
can be used with `repository.findAll`.

*Sample:*

``` java
@Service
class Service {
    private final SpecificationService specificationService;
    // ...
    List<Entity> = repository.findAll(specificationService.generateSpecification(searchRequest));
    // ...
}    
```

## Configuration

The `SpecificationService` bean only needs a `JdbcTemplate`
*Sample:*

``` java
  @Bean
  public SpecificationService specificationService(JdbcTemplate jdbcTemplate) {
    return new SpecificationService(jdbcTemplate);
  }
```

## The entity

Define the entity with usual JPA annotation

*Sample:*

``` java
@Entity
@Table(name = "entity")
public class Entity {
   @Id
   private Integer entityId;
   // ...
}
```

## Request object

This is the object containing all search parameters.

1. Use the annotation `@SearchRequestObject` for the object holding search values.
   This object typically comes from a HTTP request (request parameters or request body)
2. Use the annotation `@SearchPath` for each field of the request object to be mapped into a JPA Criteria.

*Sample:*

``` java
@SearchRequestObject
public record SearchRequest(@SearchPath("path(name) like ?") Integer entityId) {}
```

### Syntax

The `@SearchPath` annotation hold all informations to generate a JPA criteria.
These informations are a chain of operations to test a value with a field using a SQL `like` or `equal`.

1. Join
2. Values
    1. Simple values
    2. Operators
    3. Types
3. Json values

### Validation

There is an optional bean to validate all expressions set with `@SearchPath`
*Sample:*

``` java
  @Bean
  public SpecificationValidationService specificationValidationService(
      ResourceLoader resourceLoader) {
    return new SpecificationValidationService(resourceLoader) {
      @Override
      public String[] packagesToScan() {
        return new String[] {"package.that.contain.classes.annotated.with.SearchPath"};
      }
    };
  }
```

## Json and Prostgres

## Error handling

`ParseException`

`SearchException`
