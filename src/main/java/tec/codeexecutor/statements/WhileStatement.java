package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class WhileStatement implements Statement {

    @Override
    public String getName() {
        return "while";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {

        int[] ints = executor.getBlockRange();

        if (!(tokens.get(tokens.size() - 1).getKey().equals("BLb") && tokens.get(tokens.size() - 1).getVal().equals("{"))) {
            return false;
        }

        tokens.remove(tokens.size() - 1);

        if (tokens.get(0).getKey().equals("STb") && tokens.get(tokens.size() - 1).getKey().equals("STb")) {
            tokens.remove(0);
            tokens.remove(tokens.size() - 1);
        }

        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        if (!expression.getResult().getResult().getKey().equals("bol")) {
            return false;
        }

        if ((boolean)expression.getResult().getResult().getVal()) {
            executor.addVariableStateRemoveTrigger(ints[1]);
            executor.addJumpBackTrigger(ints[1]);
            executor.createVariableStateFromPrevious();
            executor.jumpToOpeningBracket();
        } else {
            executor.jumpToClosingBracket();
        }

        return true;
    }
}
