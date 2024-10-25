package com.smousseur.specification.api.exception;

/** The type Specification exception. */
public class SpecificationException extends RuntimeException {
  public SpecificationException(String message) {
    super(message);
  }

  public SpecificationException(String message, Throwable e) {
    super(message, e);
  }
}
