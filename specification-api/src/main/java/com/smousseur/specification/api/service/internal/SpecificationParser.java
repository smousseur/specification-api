package com.smousseur.specification.api.service.internal;

import com.smousseur.specification.api.antlr.ExpressionBaseVisitor;
import com.smousseur.specification.api.antlr.ExpressionLexer;
import com.smousseur.specification.api.antlr.ExpressionParser;
import com.smousseur.specification.api.criteria.*;
import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.util.Wrapper;
import java.util.ArrayList;
import java.util.List;
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
    com.smousseur.specification.api.criteria.CriteriaValueOperation operation =
        switch (currentOperation.getValue()) {
          case "eq" -> com.smousseur.specification.api.criteria.CriteriaValueOperation.EQUALS;
          case "like" -> com.smousseur.specification.api.criteria.CriteriaValueOperation.LIKE;
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
    com.smousseur.specification.api.criteria.CriteriaValueOperation operation =
        switch (currentOperation.getValue()) {
          case "eq" -> com.smousseur.specification.api.criteria.CriteriaValueOperation.EQUALS;
          case "like" -> com.smousseur.specification.api.criteria.CriteriaValueOperation.LIKE;
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

  private CriteriaObjectValue<?> getCriteriaObjectValue(
      com.smousseur.specification.api.criteria.CriteriaValueOperation operation,
      String path,
      String value) {
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
      com.smousseur.specification.api.criteria.CriteriaValueOperation operation,
      String field,
      String path,
      String value) {
    CriteriaJsonValue<?> result;
    String valueClass = currentValueType.getValue();

    CriteriaJsonValue<String> stringCriteriaJsonValue =
        new CriteriaJsonValue<>(
            field,
            path,
            operation,
            com.smousseur.specification.api.criteria.CriteriaValueType.JSON_PROPERTY,
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
      String field,
      String path,
      com.smousseur.specification.api.criteria.CriteriaValueOperation operation,
      T value,
      Class<T> type) {
    return new CriteriaJsonValue<>(
        field,
        path,
        operation,
        com.smousseur.specification.api.criteria.CriteriaValueType.JSON_PROPERTY,
        value,
        type);
  }

  private <T> CriteriaObjectValue<T> getNewCriteriaObjectValue(
      String path,
      com.smousseur.specification.api.criteria.CriteriaValueOperation operation,
      T value,
      Class<T> type) {
    return new CriteriaObjectValue<>(
        path,
        operation,
        com.smousseur.specification.api.criteria.CriteriaValueType.PROPERTY,
        value,
        type);
  }
}
