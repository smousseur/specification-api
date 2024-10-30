package com.smousseur.specification.api.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.smousseur.specification.api.criteria.Criteria;
import com.smousseur.specification.api.criteria.CriteriaJsonColumnType;
import com.smousseur.specification.api.criteria.CriteriaJsonValue;
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
}
