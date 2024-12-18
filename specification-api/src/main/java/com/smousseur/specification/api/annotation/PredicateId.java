package com.smousseur.specification.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The interface Predicate id. */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PredicateId {
  String value();
}
