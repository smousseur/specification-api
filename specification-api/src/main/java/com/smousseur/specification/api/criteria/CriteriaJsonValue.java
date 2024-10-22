package com.smousseur.specification.api.criteria;

/**
 * The type Criteria json value.
 *
 * @param <T> the type parameter
 */
public record CriteriaJsonValue<T>(
    String path,
    String jsonPath,
    CriteriaJsonColumnType columnType,
    CriteriaValueOperation operation,
    CriteriaValueType type,
    T value,
    Class<T> classz)
    implements CriteriaValue<T> {}
