package com.smousseur.specification.api.exception;

/** The type Search exception. */
public class SearchException extends RuntimeException {
  public SearchException(String message) {
    super(message);
  }

  public SearchException(String message, Throwable e) {
    super(message, e);
  }
}
