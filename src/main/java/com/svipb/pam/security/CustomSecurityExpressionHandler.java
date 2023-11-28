//package com.svipb.pam.security;
//
//import org.springframework.expression.EvaluationContext;
//import org.springframework.expression.Expression;
//import org.springframework.expression.ExpressionParser;
//import org.springframework.expression.ParserContext;
//import org.springframework.expression.spel.standard.SpelExpression;
//import org.springframework.expression.spel.standard.SpelExpressionParser;
//import org.springframework.security.access.expression.SecurityExpressionHandler;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.FilterInvocation;
//import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
//
//public class CustomSecurityExpressionHandler implements SecurityExpressionHandler <FilterInvocation>{
//
//    private final SpelExpressionParser expressionParser = new SpelExpressionParser();
//    private final DefaultWebSecurityExpressionHandler delegate = new DefaultWebSecurityExpressionHandler();
//    @Override
//    public Expression parseExpression(String expressionString, ParserContext context) {
//        return null;
//    }
//
//    @Override
//    public EvaluationContext createEvaluationContext(FilterInvocation filterInvocation) {
//        return null;
//    }
//
//    @Override
//    public ExpressionParser getExpressionParser() {
//        return null;
//    }
//
//    @Override
//    public EvaluationContext createEvaluationContext(Authentication authentication, Object invocation) {
//        return null;
//    }
//}
