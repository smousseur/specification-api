package com.smousseur.specification.api.exception;

/** The type Parse exception. */
public class ParseException extends RuntimeException {
  public ParseException(String message) {
    super(message);
  }

  public ParseException(String message, String classz, String field, String parseMessage) {
    super(String.format(message, classz, field, parseMessage));
  }
}
