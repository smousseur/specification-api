package com.smousseur.specification.api.parser;

import com.smousseur.specification.api.antlr.SpecificationBaseVisitor;
import com.smousseur.specification.api.antlr.SpecificationLexer;
import com.smousseur.specification.api.antlr.SpecificationParser;
import com.smousseur.specification.api.exception.SpecificationParseException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class SpecificationExpressionParser extends SpecificationBaseVisitor<Void> {
  private final String expression;
  private final Map<String, Predicate> predicates;
  private final CriteriaBuilder criteriaBuilder;
  private final List<Predicate> currentPredicates = new ArrayList<>();

  public SpecificationExpressionParser(
      String expression, CriteriaBuilder criteriaBuilder, Map<String, Predicate> predicates) {
    this.expression = expression;
    this.predicates = predicates;
    this.criteriaBuilder = criteriaBuilder;
  }

  public Predicate parse() {
    CharStream charStream = CharStreams.fromString(expression);
    SpecificationLexer lexer = new SpecificationLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    SpecificationParser parser = new SpecificationParser(tokens);
    SpecificationParser.SpecificationContext specification = parser.specification();
    this.visit(specification);
    if (currentPredicates.size() != 1) {
      throw new SpecificationParseException("Bad specification expression");
    }
    return currentPredicates.get(0);
  }

  @Override
  public Void visitAndPredicateCxt(SpecificationParser.AndPredicateCxtContext ctx) {
    Void unused = super.visitAndPredicateCxt(ctx);
    generatePredicate(ctx, this::andPredicates, this::andExpressions, criteriaBuilder::conjunction);
    return unused;
  }

  @Override
  public Void visitOrPredicateCxt(SpecificationParser.OrPredicateCxtContext ctx) {
    Void unused = super.visitOrPredicateCxt(ctx);
    generatePredicate(ctx, this::orPredicates, this::orExpressions, criteriaBuilder::disjunction);
    return unused;
  }

  private void generatePredicate(
      SpecificationParser.PredicateContext ctx,
      Function<Predicate[], Predicate> predicateOperator,
      BiFunction<Expression<Boolean>, Expression<Boolean>, Predicate> expressionOperator,
      Supplier<Predicate> neutral) {
    List<ParseTree> idNodes = getIdNodes(ctx);
    if (idNodes.isEmpty()) {
      Predicate orSpec = predicateOperator.apply(currentPredicates.toArray(new Predicate[0]));
      currentPredicates.clear();
      currentPredicates.add(orSpec);
    } else if (idNodes.size() == 1) {
      Predicate allSpecs = predicateOperator.apply(currentPredicates.toArray(new Predicate[0]));
      Predicate orSpec =
          expressionOperator.apply(getPredicate(idNodes.get(0).getText(), neutral), allSpecs);
      currentPredicates.clear();
      currentPredicates.add(orSpec);
    } else if (idNodes.size() == 2) {
      Predicate orSpec =
          expressionOperator.apply(
              getPredicate(idNodes.get(0).getText(), neutral),
              getPredicate(idNodes.get(1).getText(), neutral));
      currentPredicates.add(orSpec);
    }
  }

  protected Predicate andPredicates(Predicate... restrictions) {
    return Optional.ofNullable(criteriaBuilder).map(cb -> cb.and(restrictions)).orElse(null);
  }

  protected Predicate andExpressions(Expression<Boolean> x, Expression<Boolean> y) {
    return Optional.ofNullable(criteriaBuilder).map(cb -> cb.and(x, y)).orElse(null);
  }

  protected Predicate orPredicates(Predicate... restrictions) {
    return Optional.ofNullable(criteriaBuilder).map(cb -> cb.or(restrictions)).orElse(null);
  }

  protected Predicate orExpressions(Expression<Boolean> x, Expression<Boolean> y) {
    return Optional.ofNullable(criteriaBuilder).map(cb -> cb.or(x, y)).orElse(null);
  }

  private List<ParseTree> getIdNodes(SpecificationParser.PredicateContext ctx) {
    List<ParseTree> results = new ArrayList<>();
    int childCount = ctx.getChildCount();
    for (int i = 0; i < childCount; ++i) {
      if (ctx.getChild(i).getClass() == SpecificationParser.IdPredicateCxtContext.class) {
        results.add(ctx.getChild(i));
      }
    }

    return results;
  }

  private Predicate getPredicate(String predicateId, Supplier<Predicate> neutral) {
    return Optional.ofNullable(predicates.get(predicateId)).orElseGet(neutral);
  }
}
