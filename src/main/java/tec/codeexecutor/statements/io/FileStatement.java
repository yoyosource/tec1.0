package tec.codeexecutor.statements.io;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileStatement implements Statement {

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {
        if (tokens.size() == 2) {
            if (!tokens.get(1).getKey().equals("COD")) {
                return false;
            }
            String val = tokens.get(1).getVal().toString();
            if (!(val.equals("delete") || val.equals("create"))) {
                return false;
            }

            ArrayList<Token> tokenArrayList = new ArrayList<>();
            tokenArrayList.add(tokens.get(0));
            Expression expression = new Expression(tokenArrayList, variableState, executor);
            expression.build();

            String object = expression.getResult().getResult().getVal().toString();
            if (!object.startsWith("FILE:")) {
                return false;
            }

            object = object.substring("FILE:".length());

            if (val.equals("delete")) {
                File file = new File(object);
                file.delete();
            }
            if (val.equals("create")) {
                File file = new File(object);
                try {
                    file.createNewFile();
                } catch (IOException e) {

                }
            }

            return true;
        }

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
        String object = expression.getResult().getResult().getVal().toString();
        if (object.startsWith("*")) {
            object = executor.getGroundPath() + object.substring(1);
        }

        Var var = new Var(name, "FILE:" + object, type);
        if (variableState.addVar(var)) {
            return true;
        }

        return false;
    }
}
