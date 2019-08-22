package tec.codeexecutor.statements.io;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriterStatement implements Statement {

    @Override
    public String getName() {
        return "printWriter";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {
        if (tokens.size() != 3) {
            return false;
        }

        ArrayList<Token> tokens1 = new ArrayList<>();
        tokens1.add(tokens.get(0));

        ArrayList<Token> tokens2 = new ArrayList<>();
        tokens2.add(tokens.get(1));

        ArrayList<Token> tokens3 = new ArrayList<>();
        tokens3.add(tokens.get(2));

        Expression expression1 = new Expression(tokens1, variableState, executor);
        expression1.build();

        Expression expression2 = new Expression(tokens2, variableState, executor);
        expression2.build();

        Expression expression3 = new Expression(tokens3, variableState, executor);
        expression3.build();

        if (!executor.runExpressionInfo(expression1)) {
            return false;
        }

        if (!executor.runExpressionInfo(expression2)) {
            return false;
        }

        if (!executor.runExpressionInfo(expression3)) {
            return false;
        }

        if (expression1.getResult().getResult().getVal().equals("console")) {
            if ((boolean) expression2.getResult().getResult().getVal()) {
                System.out.println(expression3.getResult().getResult().getVal());
            } else {
                System.out.print(expression3.getResult().getResult().getVal());
            }
            return true;
        } else if (expression1.getResult().getResult().getVal().toString().startsWith("FILE:")) {
            try {
                File file = new File(expression1.getResult().getResult().getVal().toString().substring("FILE:".length()));
                FileWriter fr = new FileWriter(file, (boolean)expression2.getResult().getResult().getVal());
                fr.write(expression3.getResult().getResult().getVal().toString());
                fr.close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        return false;
    }
}
