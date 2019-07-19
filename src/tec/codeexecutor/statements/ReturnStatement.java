package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class ReturnStatement implements Statement {

    @Override
    public String getName() {
        return "return";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {
        if (tokens.size() == 0) {
            executor.jumpBackFunc(null);
            return true;
        }

        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        executor.jumpBackFunc(expression.getType());
        if (executor.getFuncReturn() != null) {
            executor.setFuncReturn(new Token(expression.getType(), expression.getObject()));
        }
        return true;
    }
}
