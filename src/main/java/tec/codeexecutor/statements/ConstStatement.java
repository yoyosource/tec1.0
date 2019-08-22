package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class ConstStatement implements Statement {

    @Override
    public String getName() {
        return "let";
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
        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        String type = expression.getResult().getResult().getKey();
        Object object = expression.getResult().getResult().getVal();

        Var var = new Var(name, object, type);
        var.setConstant();
        if (variableState.addVar(var)) {
            return true;
        }

        return false;
    }
}
