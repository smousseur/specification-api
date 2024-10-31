package com.smousseur.specification.api.exception;

/** The type Specification processing exception. */
public class SpecificationProcessingException extends RuntimeException {
  public SpecificationProcessingException(String message) {
    super(message);
  }

  public SpecificationProcessingException(String message, Throwable e) {
    super(message, e);
  }
}
