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

        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        if (!expression.getType().equals("lon") && !expression.getType().equals("int")) {
            return false;
        }

        try {
            if (expression.getObject() instanceof Integer) {
                Thread.sleep((long)(int) expression.getObject());
            } else {
                Thread.sleep((long) expression.getObject());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return true;
    }
}
