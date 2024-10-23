package com.smousseur.specification.api.criteria;

import com.smousseur.specification.api.exception.ParseException;
import java.util.Arrays;

/** The enum Criteria value operation. */
public enum CriteriaValueOperation {
  /** Equals criteria value operation. */
  EQUALS("="),
  /** Like criteria value operation. */
  LIKE("like"),
  /** Greater than criteria value operation. */
  GREATER_THAN(">"),
  /** Less than criteria value operation. */
  LESS_THAN("<"),
  /** Greater than or equals criteria value operation. */
  GREATER_THAN_OR_EQUALS(">="),
  /** Less than or equals criteria value operation. */
  LESS_THAN_OR_EQUALS("<=");

  private final String value;

  CriteriaValueOperation(String value) {
    this.value = value;
  }

  public static CriteriaValueOperation fromOperator(String operator) {
    return Arrays.stream(CriteriaValueOperation.values())
        .filter(op -> op.value.equals(operator))
        .findFirst()
        .orElseThrow(() -> new ParseException("Unknown operator: " + operator));
  }
}
