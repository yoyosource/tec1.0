package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

/**
 * The type If statement.
 */
public class IfStatement implements Statement {

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState) {

        if (!(tokens.get(tokens.size() - 1).getKey().equals("BLb") && tokens.get(tokens.size() - 1).getVal().toString().equals("{"))) {
            return false;
        }

        tokens.remove(tokens.size() - 1);

        if (tokens.get(tokens.size() - 1).getKey().equals("STb") && tokens.get(tokens.size() - 1).getVal().toString().equals(")")) {
            if (tokens.get(0).getKey().equals("STb") && tokens.get(0).getVal().toString().equals("(")) {
                tokens.remove(tokens.size() - 1);
                tokens.remove(0);
            }
        }

        Expression expression = new Expression(tokens, variableState);
        expression.build();

        if (!Executor.runExpressionInfo(expression)) {
            return false;
        }
        if (expression.getBoolean() == null) {
            System.out.println("ERROR: " + expression.getError());
            return false;
        }

        boolean expressionBoolean = expression.getBoolean();

        System.out.println(expressionBoolean);

        return false;
    }
}
