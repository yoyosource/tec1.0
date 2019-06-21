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
    public boolean execute(ArrayList<Token> tokens, VariableState variableState) {

        Expression expression = new Expression(tokens);
        expression.build();

        if (!Executor.runExpressionInfo(expression)) {
            return false;
        }

        if (!expression.getType().equals("num")) {
            return false;
        }

        try {
            Thread.sleep((int)expression.getObject());
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
