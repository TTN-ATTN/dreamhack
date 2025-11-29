package org.springframework.expression;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-expression-5.3.27.jar:org/springframework/expression/ExpressionParser.class */
public interface ExpressionParser {
    Expression parseExpression(String expressionString) throws ParseException;

    Expression parseExpression(String expressionString, ParserContext context) throws ParseException;
}
