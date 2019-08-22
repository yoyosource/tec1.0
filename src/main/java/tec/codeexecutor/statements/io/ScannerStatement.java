package tec.codeexecutor.statements.io;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ScannerStatement implements Statement {

    @Override
    public String getName() {
        return "scanner";
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

        String object = expression.getResult().getResult().getVal().toString();

        if (object.startsWith("FILE:")) {
            try {
                String scanner = new Scanner(new File(object.substring("FILE:".length()))).useDelimiter("\\A").next();
                Var var = new Var(name, scanner, "str");
                if (variableState.addVar(var)) {
                    return true;
                }
            } catch (FileNotFoundException | NoSuchElementException e) {
                Var var = new Var(name, "", "str");
                if (variableState.addVar(var)) {
                    return true;
                }
            }
        }
        if (object.equals("console")) {
            Scanner scanner = new Scanner(System.in);
            String text = scanner.next();
            Var var = new Var(name, text, "str");
            if (variableState.addVar(var)) {
                return true;
            }
        }

        return false;
    }
}
