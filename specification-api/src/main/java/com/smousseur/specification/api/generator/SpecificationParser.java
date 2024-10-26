package com.smousseur.specification.api.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smousseur.specification.api.antlr.ExpressionBaseVisitor;
import com.smousseur.specification.api.antlr.ExpressionLexer;
import com.smousseur.specification.api.antlr.ExpressionParser;
import com.smousseur.specification.api.criteria.*;
import com.smousseur.specification.api.criteria.Criteria;
import com.smousseur.specification.api.exception.SpecificationException;
import com.smousseur.specification.api.exception.SpecificationParseException;
import com.smousseur.specification.api.util.Utils;
import com.smousseur.specification.api.util.Wrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.*;

/** The type Specification parser service. */
public class SpecificationParser extends ExpressionBaseVisitor<Void> {
  private static final ObjectMapper mapper = new ObjectMapper();
  private final String expression;
  private final List<Criteria> criterias = new ArrayList<>();
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
    String valueClass = valueType.getValue();

    return switch (valueClass) {
      case "string" -> getNewCriteriaProperty(value, String.class);
      case "int" -> getNewCriteriaProperty(Integer.valueOf(value), Integer.class);
      case "long" -> getNewCriteriaProperty(Long.valueOf(value), Long.class);
      case "float" -> getNewCriteriaProperty(Float.valueOf(value), Float.class);
      case "double" -> getNewCriteriaProperty(Double.valueOf(value), Double.class);
      case "bool" -> getNewCriteriaProperty(Boolean.valueOf(value), Boolean.class);
      case "date" -> getNewCriteriaProperty(LocalDate.parse(value), LocalDate.class);
      case "datetime" -> getNewCriteriaProperty(LocalDateTime.parse(value), LocalDateTime.class);
      case "list" -> {
        try {
          yield getNewCriteriaProperty(
              mapper.readValue(value, new TypeReference<List<?>>() {}), List.class);
        } catch (JsonProcessingException e) {
          throw new SpecificationException(
              "Cannot deserialize list for property: " + valuePath.getValue(), e);
        }
      }
      default -> throw new UnsupportedOperationException("Type not supported");
    };
  }

  private CriteriaJsonValue<?> getCriteriaJsonValue(String value) {
    String valueClass = valueType.getValue();
    String path = valuePath.getValue();
    String jsonPath = jsonValuePath.getValue();
    CriteriaOperation operationValue = operation.getValue();

    return switch (valueClass) {
      case "string" ->
          getNewCriteriaJsonProperty(path, jsonPath, operationValue, value, String.class);
      case "int" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, Integer.valueOf(value), Integer.class);
      case "long" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, Long.valueOf(value), Long.class);
      case "float" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, Float.valueOf(value), Float.class);
      case "double" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, Double.valueOf(value), Double.class);
      case "bool" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, Boolean.valueOf(value), Boolean.class);
      case "date" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, LocalDate.parse(value), LocalDate.class);
      case "datetime" ->
          getNewCriteriaJsonProperty(
              path, jsonPath, operationValue, LocalDateTime.parse(value), LocalDateTime.class);
      case "list" -> {
        try {
          yield getNewCriteriaJsonProperty(
              path,
              jsonPath,
              operationValue,
              mapper.readValue(value, new TypeReference<List<?>>() {}),
              List.class);
        } catch (JsonProcessingException e) {
          throw new SpecificationException(
              "Cannot deserialize list for property: " + valuePath.getValue(), e);
        }
      }
      default -> throw new UnsupportedOperationException("Type not supported");
    };
  }

  private <T> CriteriaJsonValue<T> getNewCriteriaJsonProperty(
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
                      throw new SpecificationParseException("Unknown json column type " + val);
                })
        .orElse(CriteriaJsonColumnType.JSONB);
  }

  private <T> CriteriaObjectValue<T> getNewCriteriaProperty(T value, Class<T> type) {
    return new CriteriaObjectValue<>(
        valuePath.getValue(), operation.getValue(), CriteriaValueType.PROPERTY, value, type);
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
