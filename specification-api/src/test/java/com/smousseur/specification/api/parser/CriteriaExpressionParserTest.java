package com.smousseur.specification.api.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.smousseur.specification.api.criteria.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class CriteriaExpressionParserTest {
  @Test
  void testParseJsonExpression() {
    String expression = "join(address)->json_property(location, coord.x) = ?";
    CriteriaExpressionParser parser = new CriteriaExpressionParser("criteria1", expression, 10);
    List<Criteria> criterias = parser.parse();
    assertEquals(2, criterias.size());
    assertEquals("address", criterias.get(0).path());
    CriteriaJsonValue jsonCriteria = (CriteriaJsonValue) criterias.get(1);
    assertEquals("location", jsonCriteria.path());
    assertEquals("coord.x", jsonCriteria.jsonPath());
    assertEquals(CriteriaJsonColumnType.JSONB, jsonCriteria.columnType());
    assertEquals(Integer.class, jsonCriteria.value().getClass());
  }

  @Test
  void testParseOperators() {
    String expression = "property(location) != ?";
    CriteriaExpressionParser parser = new CriteriaExpressionParser("criteria1", expression, 10);
    List<Criteria> criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.NOT_EQUALS, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) like ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.LIKE, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) in ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.IN, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) contains ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.CONTAINS, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) in ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.IN, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) > ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.GREATER_THAN, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) >= ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(
        CriteriaOperation.GREATER_THAN_OR_EQUALS, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) < ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.LESS_THAN, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) <= ?";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(
        CriteriaOperation.LESS_THAN_OR_EQUALS, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) isnull";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.ISNULL, ((CriteriaValue) criterias.get(0)).operation());
    expression = "property(location) isnotnull";
    parser = new CriteriaExpressionParser("criteria1", expression, 10);
    criterias = parser.parse();
    assertEquals(1, criterias.size());
    assertSame(CriteriaOperation.ISNOTNULL, ((CriteriaValue) criterias.get(0)).operation());
  }
}
