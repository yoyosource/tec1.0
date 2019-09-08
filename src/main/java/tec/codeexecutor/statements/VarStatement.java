package tec.codeexecutor.statements;

import tec.codeexecutor.*;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Var statement.
 */
public class VarStatement implements Statement {

    @Override
    public String getName() {
        return "var";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {
        if (tokens.size() < 3) {
            return false;
        }

        String name = tokens.get(0).getVal().toString();

        if (!tokens.get(1).getKey().equals("ASG")) {
            return false;
        }

        tokens.remove(0);
        tokens.remove(0);

        if (tokens.get(0).getKey().equals("ACb") && tokens.get(0).getVal().equals("[") && tokens.get(tokens.size() - 1).getKey().equals("ACb") && tokens.get(tokens.size() - 1).getVal().equals("]")) {
            tokens.remove(0);
            tokens.remove(tokens.size() - 1);

            List<List<Token>> toktoks = ExpressionUtils.getSplitTokens(tokens);

            ExpressionState expressionState = new ExpressionState();
            List<Token> tokenList = ExpressionUtils.evaluateTokenList(toktoks, variableState, executor, expressionState);


            if (!executor.runExpressionInfo(expressionState)) {
                return false;
            }

            System.out.println(tokenList);

            List<Object> objects = new ArrayList<>();

            for (Token token : tokenList) {
                objects.add(token.getVal());
            }

            Var var = new Var(name, objects, "int");
            var.setArray();
            if (variableState.addVar(var)) {
                return true;
            }
        } else {
            Expression expression = new Expression(tokens, variableState, executor);
            expression.build();

            if (!executor.runExpressionInfo(expression)) {
                return false;
            }

            String type = expression.getResult().getResult().getKey();
            Object object = expression.getResult().getResult().getVal();

            Var var = new Var(name, object, type);
            if (variableState.addVar(var)) {
                return true;
            }
        }

        return false;
    }
}
