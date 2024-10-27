package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.antlr.ExpressionBaseVisitor;
import com.smousseur.specification.api.antlr.ExpressionLexer;
import com.smousseur.specification.api.antlr.ExpressionParser;
import com.smousseur.specification.api.criteria.*;
import com.smousseur.specification.api.criteria.Criteria;
import com.smousseur.specification.api.exception.SpecificationParseException;
import com.smousseur.specification.api.util.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.*;

/** The type Specification parser service. */
public class SpecificationParser extends ExpressionBaseVisitor<Void> {
  private final String expression;
  private final Object value;
  private final List<Criteria> criterias = new ArrayList<>();
  private final Wrapper<CriteriaOperation> operation = new Wrapper<>();
  private final Wrapper<String> jsonOption = new Wrapper<>();
  private final Wrapper<String> valuePath = new Wrapper<>();
  private final Wrapper<String> jsonValuePath = new Wrapper<>();

  public SpecificationParser(String expression, Object value) {
    this.expression = expression;
    this.value = value;
  }

  /**
   * Parse list.
   *
   * @return the list
   */
  public List<Criteria> parse() {
    CharStream charStream = CharStreams.fromString(expression);
    ExpressionLexer lexer = new ExpressionLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ExpressionParser parser = new ExpressionParser(tokens);
    addParserErrorListener(parser);
    this.visit(parser.expression());
    return criterias;
  }

  @Override
  public Void visitExpression(ExpressionParser.ExpressionContext ctx) {
    Void unused = super.visitExpression(ctx);
    if (jsonValuePath.isEmpty()) {
      criterias.add(
          new CriteriaObjectValue(
              valuePath.getValue(), operation.getValue(), CriteriaValueType.PROPERTY, value));
    } else {
      criterias.add(getCriteriaJsonValue());
    }
    return unused;
  }

  @Override
  public Void visitJoin(ExpressionParser.JoinContext ctx) {
    criterias.add(new CriteriaJoin(ctx.IDENTIFIER().getText()));
    return super.visitJoin(ctx);
  }

  @Override
  public Void visitProperty(ExpressionParser.PropertyContext ctx) {
    Void unused = super.visitProperty(ctx);
    valuePath.setValue(ctx.IDENTIFIER().getText());
    return unused;
  }

  @Override
  public Void visitJsonProperty(ExpressionParser.JsonPropertyContext ctx) {
    Void unused = super.visitJsonProperty(ctx);
    valuePath.setValue(ctx.IDENTIFIER(0).getText());
    jsonValuePath.setValue(ctx.IDENTIFIER(1).getText());
    return unused;
  }

  @Override
  public Void visitOperator(ExpressionParser.OperatorContext ctx) {
    operation.setValue(CriteriaOperation.fromOperator(ctx.getText()));
    return super.visitOperator(ctx);
  }

  @Override
  public Void visitJsonOption(ExpressionParser.JsonOptionContext ctx) {
    jsonOption.setValue(ctx.getText());
    return super.visitJsonOption(ctx);
  }

  private CriteriaJsonValue getCriteriaJsonValue() {
    String path = valuePath.getValue();
    String jsonPath = jsonValuePath.getValue();
    CriteriaJsonColumnType columnType = getCriteriaJsonColumnType();

    return new CriteriaJsonValue(
        path, jsonPath, columnType, operation.getValue(), CriteriaValueType.JSON_PROPERTY, value);
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

  private static void addParserErrorListener(ExpressionParser parser) {
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
