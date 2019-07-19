package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The type If statement.
 */
public class IfStatement implements Statement {

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {

        int[] ints = executor.getIfRange();

        ArrayList<Boolean> booleans = new ArrayList<>();
        ArrayList<Integer> jumpPoint = new ArrayList<>();

        for (int i = 0; i < ints.length - 2; i += 2) {
            int start = ints[i];
            int stop = ints[i + 1];

            ArrayList<Token> tokenArrayList = executor.getRange(start, stop);

            if (tokenArrayList.get(0).getKey().equals("STb") && tokenArrayList.get(tokenArrayList.size() - 1).getKey().equals("STb")) {
                tokenArrayList.remove(0);
                tokenArrayList.remove(tokenArrayList.size() - 1);
            }

            Expression expression = new Expression(tokenArrayList, variableState, executor);
            expression.build();

            if (!executor.runExpressionInfo(expression)) {
                return false;
            }

            jumpPoint.add(stop + 1);
            booleans.add(expression.getBoolean());
        }

        if (ints[ints.length - 1] < jumpPoint.get(jumpPoint.size() - 1)) {
            jumpPoint.add(executor.getClosingBlock(ints[ints.length - 1]));
        } else {
            jumpPoint.add(ints[ints.length - 1]);
        }
        booleans.add(true);

        int end = executor.getBlockRange(ints[ints.length - 1])[1];

        for (int i = 0; i < booleans.size(); i++) {
            if (booleans.get(i).booleanValue()) {
                executor.jumpToIndex(jumpPoint.get(i));
                executor.addVariableStateRemoveTrigger(end);
                executor.createVariableStateFromPrevious();
                return true;
            }
        }

        return false;
    }
}
