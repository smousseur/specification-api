package com.smousseur.specification.api.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.criteria.CriteriaJsonColumnType;
import com.smousseur.specification.api.criteria.CriteriaJsonValue;
import com.smousseur.specification.api.criteria.CriteriaValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpecificationParserTest {
  @Test
  @SuppressWarnings("rawtypes")
  void testParseJsonExpression() {
    String expression = "join(address)->json_path(location, coord.x, int) = \"10\"";
    SpecificationParser parser = new SpecificationParser(expression);
    List<AbstractCriteria> criterias = parser.parse();
    assertEquals(2, criterias.size());
    assertEquals("address", criterias.get(0).path());
    CriteriaJsonValue jsonCriteria = (CriteriaJsonValue) criterias.get(1);
    assertEquals("location", jsonCriteria.path());
    assertEquals("coord.x", jsonCriteria.jsonPath());
    assertEquals(CriteriaJsonColumnType.JSONB, jsonCriteria.columnType());
    assertEquals(Integer.class, jsonCriteria.classz());
  }

  @Test
  void testQuotedExpression() {
    String expression = "join(address)->json_path(location, coord.x) like \"prout\"\"";
    SpecificationParser parser = new SpecificationParser(expression);
    List<AbstractCriteria> criterias = parser.parse();
    assertEquals(2, criterias.size());
    assertEquals("address", criterias.get(0).path());
    assertEquals("prout\"", ((CriteriaValue<?>) criterias.get(1)).value());
  }
}
