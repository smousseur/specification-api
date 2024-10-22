package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.antlr.ExpressionBaseVisitor;
import com.smousseur.specification.api.antlr.ExpressionLexer;
import com.smousseur.specification.api.antlr.ExpressionParser;
import com.smousseur.specification.api.criteria.*;
import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.exception.ParseException;
import com.smousseur.specification.api.util.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/** The type Specification parser service. */
public class SpecificationParser extends ExpressionBaseVisitor<Void> {
  private final String expression;

  /** The Criterias. */
  private final List<AbstractCriteria> criterias = new ArrayList<>();

  /** The Current operation. */
  private final Wrapper<String> currentOperation = new Wrapper<>();

  /** The Current value type. */
  private final Wrapper<String> currentValueType = new Wrapper<>();

  private final Wrapper<String> currentJsonOption = new Wrapper<>();

  public SpecificationParser(String expression) {
    this.expression = expression;
  }

  /**
   * Parse list.
   *
   * @return the list
   */
  public List<AbstractCriteria> parse() {
    CharStream charStream = CharStreams.fromString(expression);
    ExpressionLexer lexer = new ExpressionLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ExpressionParser parser = new ExpressionParser(tokens);
    this.visit(parser.expression());
    return criterias;
  }

  @Override
  public Void visitJoin(ExpressionParser.JoinContext ctx) {
    criterias.add(new CriteriaNode(ctx.IDENTIFIER().get(0).getText()));
    return super.visitJoin(ctx);
  }

  @Override
  public Void visitObjectValue(ExpressionParser.ObjectValueContext ctx) {
    Void result = super.visitObjectValue(ctx);
    CriteriaValueOperation operation =
        switch (currentOperation.getValue()) {
          case "eq" -> CriteriaValueOperation.EQUALS;
          case "like" -> CriteriaValueOperation.LIKE;
          default -> throw new UnsupportedOperationException("Operation not supported");
        };
    String path = ctx.IDENTIFIER().get(0).getText();
    String value = ctx.IDENTIFIER().get(1).getText();
    criterias.add(getCriteriaObjectValue(operation, path, value));
    return result;
  }

  @Override
  public Void visitJsonValue(ExpressionParser.JsonValueContext ctx) {
    Void result = super.visitJsonValue(ctx);
    CriteriaValueOperation operation =
        switch (currentOperation.getValue()) {
          case "eq" -> CriteriaValueOperation.EQUALS;
          case "like" -> CriteriaValueOperation.LIKE;
          default -> throw new UnsupportedOperationException("Operation not supported");
        };
    String field = ctx.IDENTIFIER().get(0).getText();
    String path = ctx.IDENTIFIER().get(1).getText();
    String value = ctx.IDENTIFIER().get(2).getText();
    criterias.add(getCriteriaJsonValue(operation, field, path, value));
    return result;
  }

  @Override
  public Void visitValueOperation(ExpressionParser.ValueOperationContext ctx) {
    currentOperation.setValue(ctx.getText());
    return super.visitValueOperation(ctx);
  }

  @Override
  public Void visitValueType(ExpressionParser.ValueTypeContext ctx) {
    currentValueType.setValue(ctx.getText());
    return super.visitValueType(ctx);
  }

  @Override
  public Void visitJsonOption(ExpressionParser.JsonOptionContext ctx) {
    currentJsonOption.setValue(ctx.getText());
    return super.visitJsonOption(ctx);
  }

  private CriteriaObjectValue<?> getCriteriaObjectValue(
      CriteriaValueOperation operation, String path, String value) {
    CriteriaObjectValue<?> result;
    String valueClass = currentValueType.getValue();

    if (valueClass != null) {
      result =
          switch (valueClass) {
            case "string" -> getNewCriteriaObjectValue(path, operation, value, String.class);
            case "int" ->
                getNewCriteriaObjectValue(path, operation, Integer.valueOf(value), Integer.class);
            case "long" ->
                getNewCriteriaObjectValue(path, operation, Long.valueOf(value), Long.class);
            case "float" ->
                getNewCriteriaObjectValue(path, operation, Float.valueOf(value), Float.class);
            case "double" ->
                getNewCriteriaObjectValue(path, operation, Double.valueOf(value), Double.class);
            case "bool" ->
                getNewCriteriaObjectValue(path, operation, Boolean.valueOf(value), Boolean.class);
            default -> throw new UnsupportedOperationException("Type not supported");
          };
    } else {
      result = getNewCriteriaObjectValue(path, operation, value, String.class);
    }

    return result;
  }

  private CriteriaJsonValue<?> getCriteriaJsonValue(
      CriteriaValueOperation operation, String field, String path, String value) {
    CriteriaJsonValue<?> result;
    String valueClass = currentValueType.getValue();
    CriteriaJsonColumnType columnType = getCriteriaJsonColumnType();

    CriteriaJsonValue<String> stringCriteriaJsonValue =
        new CriteriaJsonValue<>(
            field,
            path,
            columnType,
            operation,
            CriteriaValueType.JSON_PROPERTY,
            value,
            String.class);
    if (valueClass != null) {
      result =
          switch (valueClass) {
            case "string" -> stringCriteriaJsonValue;
            case "int" ->
                getNewCriteriaJsonValue(
                    field, path, operation, Integer.valueOf(value), Integer.class);
            case "long" ->
                getNewCriteriaJsonValue(field, path, operation, Long.valueOf(value), Long.class);
            case "float" ->
                getNewCriteriaJsonValue(field, path, operation, Float.valueOf(value), Float.class);
            case "double" ->
                getNewCriteriaJsonValue(
                    field, path, operation, Double.valueOf(value), Double.class);
            case "bool" ->
                getNewCriteriaJsonValue(
                    field, path, operation, Boolean.valueOf(value), Boolean.class);
            default -> throw new UnsupportedOperationException("Type not supported");
          };
    } else {
      result = stringCriteriaJsonValue;
    }

    return result;
  }

  private <T> CriteriaJsonValue<T> getNewCriteriaJsonValue(
      String field, String path, CriteriaValueOperation operation, T value, Class<T> type) {
    CriteriaJsonColumnType columnType = getCriteriaJsonColumnType();
    return new CriteriaJsonValue<>(
        field, path, columnType, operation, CriteriaValueType.JSON_PROPERTY, value, type);
  }

  private CriteriaJsonColumnType getCriteriaJsonColumnType() {
    return Optional.ofNullable(currentJsonOption.getValue())
        .map(
            val ->
                switch (val) {
                  case "json" -> CriteriaJsonColumnType.JSON;
                  case "jsonb" -> CriteriaJsonColumnType.JSONB;
                  default ->
                      throw new ParseException(String.format("Unknown json column type %s", val));
                })
        .orElse(CriteriaJsonColumnType.JSONB);
  }

  private <T> CriteriaObjectValue<T> getNewCriteriaObjectValue(
      String path, CriteriaValueOperation operation, T value, Class<T> type) {
    return new CriteriaObjectValue<>(path, operation, CriteriaValueType.PROPERTY, value, type);
  }
}
