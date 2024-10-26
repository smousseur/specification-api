package com.smousseur.specification.api.criteria;

import com.smousseur.specification.api.exception.SpecificationParseException;
import java.util.Arrays;

/** The enum Criteria value operation. */
public enum CriteriaOperation {
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
  LESS_THAN_OR_EQUALS("<="),
  /** Contains criteria operation. */
  CONTAINS("contains"),
  /** In criteria operation. */
  IN("in");

  private final String value;

  CriteriaOperation(String value) {
    this.value = value;
  }

  public static CriteriaOperation fromOperator(String operator) {
    return Arrays.stream(CriteriaOperation.values())
        .filter(op -> op.value.equals(operator))
        .findFirst()
        .orElseThrow(() -> new SpecificationParseException("Unknown operator: " + operator));
  }
}
