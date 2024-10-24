package com.smousseur.specification.api.generator;

import com.smousseur.specification.api.antlr.ExpressionBaseVisitor;
import com.smousseur.specification.api.antlr.ExpressionLexer;
import com.smousseur.specification.api.antlr.ExpressionParser;
import com.smousseur.specification.api.criteria.*;
import com.smousseur.specification.api.criteria.AbstractCriteria;
import com.smousseur.specification.api.exception.ParseException;
import com.smousseur.specification.api.util.Utils;
import com.smousseur.specification.api.util.Wrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/** The type Specification parser service. */
public class SpecificationParser extends ExpressionBaseVisitor<Void> {
  private final String expression;
  private final List<AbstractCriteria> criterias = new ArrayList<>();
  private final Wrapper<CriteriaOperation> operation = new Wrapper<>();
  private final Wrapper<String> valueType = new Wrapper<>();
  private final Wrapper<String> jsonOption = new Wrapper<>();
  private final Wrapper<String> valuePath = new Wrapper<>();
  private final Wrapper<String> jsonValuePath = new Wrapper<>();

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
  public Void visitExpression(ExpressionParser.ExpressionContext ctx) {
    Void unused = super.visitExpression(ctx);
    String value = ctx.VALUE().getText();
    value = value.replaceFirst("\"", "");
    value = Utils.replaceLast(value, "\"", "");
    if (jsonValuePath.isEmpty()) {
      criterias.add(getCriteriaObjectValue(value));
    } else {
      criterias.add(getCriteriaJsonValue(value));
    }
    return unused;
  }

  @Override
  public Void visitJoin(ExpressionParser.JoinContext ctx) {
    criterias.add(new CriteriaJoin(ctx.IDENTIFIER().getText()));
    return super.visitJoin(ctx);
  }

  @Override
  public Void visitObjectPath(ExpressionParser.ObjectPathContext ctx) {
    Void unused = super.visitObjectPath(ctx);
    valuePath.setValue(ctx.IDENTIFIER().getText());
    return unused;
  }

  @Override
  public Void visitJsonPath(ExpressionParser.JsonPathContext ctx) {
    Void unused = super.visitJsonPath(ctx);
    valuePath.setValue(ctx.IDENTIFIER(0).getText());
    jsonValuePath.setValue(ctx.IDENTIFIER(1).getText());
    return unused;
  }

  @Override
  public Void visitValueType(ExpressionParser.ValueTypeContext ctx) {
    Void unused = super.visitValueType(ctx);
    valueType.setValue(ctx.getText());
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

  private CriteriaObjectValue<?> getCriteriaObjectValue(String value) {
    CriteriaObjectValue<?> result;
    String valueClass = valueType.getValue();

    if (valueClass != null) {
      result =
          switch (valueClass) {
            case "string" -> getNewCriteriaObjectValue(value, String.class);
            case "int" -> getNewCriteriaObjectValue(Integer.valueOf(value), Integer.class);
            case "long" -> getNewCriteriaObjectValue(Long.valueOf(value), Long.class);
            case "float" -> getNewCriteriaObjectValue(Float.valueOf(value), Float.class);
            case "double" -> getNewCriteriaObjectValue(Double.valueOf(value), Double.class);
            case "bool" -> getNewCriteriaObjectValue(Boolean.valueOf(value), Boolean.class);
            case "date" -> getNewCriteriaObjectValue(LocalDate.parse(value), LocalDate.class);
            case "datetime" ->
                getNewCriteriaObjectValue(LocalDateTime.parse(value), LocalDateTime.class);
            default -> throw new UnsupportedOperationException("Type not supported");
          };
    } else {
      result = getNewCriteriaObjectValue(value, String.class);
    }

    return result;
  }

  private CriteriaJsonValue<?> getCriteriaJsonValue(String value) {
    CriteriaJsonValue<?> result;
    String valueClass = valueType.getValue();
    CriteriaJsonColumnType columnType = getCriteriaJsonColumnType();
    String path = valuePath.getValue();
    String jsonPath = jsonValuePath.getValue();
    CriteriaJsonValue<String> stringCriteriaJsonValue =
        new CriteriaJsonValue<>(
            path,
            jsonPath,
            columnType,
            operation.getValue(),
            CriteriaValueType.JSON_PROPERTY,
            value,
            String.class);
    if (valueClass != null) {
      result =
          switch (valueClass) {
            case "string" -> stringCriteriaJsonValue;
            case "int" ->
                getNewCriteriaJsonValue(
                    path, jsonPath, operation.getValue(), Integer.valueOf(value), Integer.class);
            case "long" ->
                getNewCriteriaJsonValue(
                    path, jsonPath, operation.getValue(), Long.valueOf(value), Long.class);
            case "float" ->
                getNewCriteriaJsonValue(
                    path, jsonPath, operation.getValue(), Float.valueOf(value), Float.class);
            case "double" ->
                getNewCriteriaJsonValue(
                    path, jsonPath, operation.getValue(), Double.valueOf(value), Double.class);
            case "bool" ->
                getNewCriteriaJsonValue(
                    path, jsonPath, operation.getValue(), Boolean.valueOf(value), Boolean.class);
            default -> throw new UnsupportedOperationException("Type not supported");
          };
    } else {
      result = stringCriteriaJsonValue;
    }

    return result;
  }

  private <T> CriteriaJsonValue<T> getNewCriteriaJsonValue(
          String field, String path, CriteriaOperation operation, T value, Class<T> type) {
    CriteriaJsonColumnType columnType = getCriteriaJsonColumnType();
    return new CriteriaJsonValue<>(
        field, path, columnType, operation, CriteriaValueType.JSON_PROPERTY, value, type);
  }

  private CriteriaJsonColumnType getCriteriaJsonColumnType() {
    return Optional.ofNullable(jsonOption.getValue())
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

  private <T> CriteriaObjectValue<T> getNewCriteriaObjectValue(T value, Class<T> type) {
    return new CriteriaObjectValue<>(
        valuePath.getValue(), operation.getValue(), CriteriaValueType.PROPERTY, value, type);
  }
}
