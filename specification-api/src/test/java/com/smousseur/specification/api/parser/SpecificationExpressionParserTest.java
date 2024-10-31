package com.smousseur.specification.api.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SpecificationExpressionParserTest {
  @Test
  void testSpecificationExpression() {
    String expression = "(pName && pAddress or pStreet) && pCity";
    SpecificationExpressionParser parser =
        new SpecificationExpressionParser(expression, null, getPredicates());
    assertDoesNotThrow(parser::parse);
  }

  private Map<String, Predicate> getPredicates() {
    HashMap<String, Predicate> results = new HashMap<>();
    results.put("pName", new DummyPredicate());
    results.put("pAddress", new DummyPredicate());
    results.put("pStreet", new DummyPredicate());
    results.put("pCity", new DummyPredicate());
    return results;
  }

  static class DummyPredicate implements Predicate {
    @Override
    public BooleanOperator getOperator() {
      return null;
    }

    @Override
    public boolean isNegated() {
      return false;
    }

    @Override
    public List<Expression<Boolean>> getExpressions() {
      return null;
    }

    @Override
    public Predicate not() {
      return null;
    }

    @Override
    public Predicate isNull() {
      return null;
    }

    @Override
    public Predicate isNotNull() {
      return null;
    }

    @Override
    public Predicate in(Object... values) {
      return null;
    }

    @Override
    public Predicate in(Expression<?>... values) {
      return null;
    }

    @Override
    public Predicate in(Collection<?> values) {
      return null;
    }

    @Override
    public Predicate in(Expression<Collection<?>> values) {
      return null;
    }

    @Override
    public <X> Expression<X> as(Class<X> type) {
      return null;
    }

    @Override
    public Selection<Boolean> alias(String name) {
      return null;
    }

    @Override
    public boolean isCompoundSelection() {
      return false;
    }

    @Override
    public List<Selection<?>> getCompoundSelectionItems() {
      return null;
    }

    @Override
    public Class<? extends Boolean> getJavaType() {
      return null;
    }

    @Override
    public String getAlias() {
      return null;
    }
  }
}
