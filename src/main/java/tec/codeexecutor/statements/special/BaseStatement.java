package tec.codeexecutor.statements.special;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Base64;

public class BaseStatement implements Statement {

    @Override
    public String getName() {
        return "base64";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {

        if (!tokens.get(0).getKey().equals("COD")) {
            return false;
        }

        String type = tokens.get(0).getVal().toString();
        tokens.remove(0);

        if (!tokens.get(0).getKey().equals("COD")) {
            return false;
        }

        String varName = tokens.get(0).getVal().toString();
        tokens.remove(0);

        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        if (type.equals("encode")) {
            try {
                byte[] bytes = Base64.getEncoder().encode((expression.getResult().getResult().getVal() + "").getBytes());
                String s = new String(bytes);
                variableState.addVar(new Var(varName, s, "str"));
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if (type.equals("decode")) {
            try {
                byte[] bytes = Base64.getDecoder().decode((expression.getResult().getResult().getVal() + "").getBytes());
                String s = new String(bytes);
                variableState.addVar(new Var(varName, s, "str"));
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
