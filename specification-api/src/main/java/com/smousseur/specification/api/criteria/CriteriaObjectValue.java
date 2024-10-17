package com.smousseur.specification.api.criteria;

public record CriteriaObjectValue<T>(
    String path, CriteriaValueOperation operation, CriteriaValueType type, T value, Class<T> classz)
    implements CriteriaValue<T> {}
