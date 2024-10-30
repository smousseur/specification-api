package com.smousseur.specification.api.parser;

import com.smousseur.specification.api.antlr.CriteriaBaseVisitor;
import com.smousseur.specification.api.antlr.CriteriaLexer;
import com.smousseur.specification.api.antlr.CriteriaParser;
import com.smousseur.specification.api.criteria.*;
import com.smousseur.specification.api.criteria.Criteria;
import com.smousseur.specification.api.exception.SpecificationParseException;
import com.smousseur.specification.api.util.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.*;

/** The type Specification parser service. */
public class CriteriaExpressionParser extends CriteriaBaseVisitor<Void> {
  private final String expression;
  private final Object value;
  private final String criteriaId;
  private final List<Criteria> criterias = new ArrayList<>();
  private final Wrapper<CriteriaOperation> operation = new Wrapper<>();
  private final Wrapper<String> jsonOption = new Wrapper<>();
  private final Wrapper<String> valuePath = new Wrapper<>();
  private final Wrapper<String> jsonValuePath = new Wrapper<>();

  public CriteriaExpressionParser(String criteriaId, String expression, Object value) {
    this.expression = expression;
    this.value = value;
    this.criteriaId = criteriaId;
  }

  /**
   * Parse list.
   *
   * @return the list
   */
  public List<Criteria> parse() {
    CharStream charStream = CharStreams.fromString(expression);
    CriteriaLexer lexer = new CriteriaLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CriteriaParser parser = new CriteriaParser(tokens);
    addParserErrorListener(parser);
    this.visit(parser.criteria());
    return criterias;
  }

  @Override
  public Void visitCriteria(CriteriaParser.CriteriaContext ctx) {
    Void unused = super.visitCriteria(ctx);
    if (jsonValuePath.isEmpty()) {
      criterias.add(
          new CriteriaObjectValue(
              criteriaId,
              valuePath.getValue(),
              operation.getValue(),
              CriteriaValueType.PROPERTY,
              value));
    } else {
      criterias.add(getCriteriaJsonValue());
    }
    return unused;
  }

  @Override
  public Void visitJoin(CriteriaParser.JoinContext ctx) {
    criterias.add(new CriteriaJoin(ctx.IDENTIFIER().getText()));
    return super.visitJoin(ctx);
  }

  @Override
  public Void visitProperty(CriteriaParser.PropertyContext ctx) {
    Void unused = super.visitProperty(ctx);
    valuePath.setValue(ctx.IDENTIFIER().getText());
    return unused;
  }

  @Override
  public Void visitJsonProperty(CriteriaParser.JsonPropertyContext ctx) {
    Void unused = super.visitJsonProperty(ctx);
    valuePath.setValue(ctx.IDENTIFIER(0).getText());
    jsonValuePath.setValue(ctx.IDENTIFIER(1).getText());
    return unused;
  }

  @Override
  public Void visitOperator(CriteriaParser.OperatorContext ctx) {
    operation.setValue(CriteriaOperation.fromOperator(ctx.getText()));
    return super.visitOperator(ctx);
  }

  @Override
  public Void visitJsonOption(CriteriaParser.JsonOptionContext ctx) {
    jsonOption.setValue(ctx.getText());
    return super.visitJsonOption(ctx);
  }

  private CriteriaJsonValue getCriteriaJsonValue() {
    String path = valuePath.getValue();
    String jsonPath = jsonValuePath.getValue();
    CriteriaJsonColumnType columnType = getCriteriaJsonColumnType();

    return new CriteriaJsonValue(
        criteriaId,
        path,
        jsonPath,
        columnType,
        operation.getValue(),
        CriteriaValueType.JSON_PROPERTY,
        value);
  }

  private CriteriaJsonColumnType getCriteriaJsonColumnType() {
    return Optional.ofNullable(jsonOption.getValue())
        .map(
            val ->
                switch (val) {
                  case "json" -> CriteriaJsonColumnType.JSON;
                  case "jsonb" -> CriteriaJsonColumnType.JSONB;
                  default ->
                      throw new SpecificationParseException("Unknown json column type " + val);
                })
        .orElse(CriteriaJsonColumnType.JSONB);
  }

  private static void addParserErrorListener(CriteriaParser parser) {
    parser.addErrorListener(
        new BaseErrorListener() {
          @Override
          public void syntaxError(
              Recognizer<?, ?> recognizer,
              Object offendingSymbol,
              int line,
              int charPositionInLine,
              String msg,
              RecognitionException e) {
            throw new SpecificationParseException("Syntax error: " + msg);
          }
        });
  }
}
