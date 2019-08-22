package test;

import tec.codeexecutor.*;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.List;

public class Work {

    public static void main(String[] args) {
        String s = "\"hi\".repeat(\"hi\".length())";

        Lexer lexer = new Lexer();
        lexer.createTokens(s, false);

        List<Token> tokens = copy(lexer.getTokens());

        VariableState variableState = new VariableState();
        variableState.addVar(new Var("i", 2, "int"));
        variableState.addVar(new Var("j", 1.0, "num"));
        variableState.addVar(new Var("k", "Hello World", "str"));
        variableState.addVar(new Var("l", System.currentTimeMillis(), "lon"));
        variableState.addVar(new Var("m", 'c', "chr"));

        Expression expression = new Expression(tokens, variableState, new Executor(lexer.getTokens(), new Implementor()));
        expression.build();

        ExpressionState expressionState = expression.getResult();

        if (expressionState.hasErrors()) {
            boolean b = false;
            for (String e : expressionState.getErrors()) {
                if (b) {
                    System.out.println("");
                }
                System.out.println(e);
                b = true;
            }
        } else {
            System.out.println(expressionState.getResult().compact());
        }
        System.out.println("Time Elapsed: " + expressionState.getTime() + "ms");
    }

    private static List<Token> copy(List<Token> tokens) {
        List<Token> list = new ArrayList<>();
        for (Token token : tokens) {
            list.add(token);
        }
        return list;
    }

}
