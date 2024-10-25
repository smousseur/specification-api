package com.smousseur.specification.api.exception;

/** The type Specification parse exception. */
public class SpecificationParseException extends RuntimeException {
  public SpecificationParseException(String message) {
    super(message);
  }

  public SpecificationParseException(
      String message, String classz, String field, String parseMessage) {
    super(String.format(message, classz, field, parseMessage));
  }
}
