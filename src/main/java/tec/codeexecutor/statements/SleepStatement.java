package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class SleepStatement implements Statement {

    @Override
    public String getName() {
        return "sleep";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {

        Expression expression = new Expression(tokens);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        if (!expression.getType().equals("num")) {
            return false;
        }

        if (!expression.getType().equals("int")) {
            return false;
        }

        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < (int)expression.getObject()) {

        }

        return true;
    }
}
