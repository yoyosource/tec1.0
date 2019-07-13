package tec.codeexecutor.statements;

import tec.Tec;
import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

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
        Expression expression = new Expression(tokens, variableState);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        String type = expression.getType();
        Object object = expression.getObject();

        Var var = new Var(name, object, type);
        if (variableState.addVar(var)) {
            return true;
        }

        return false;
    }
}
