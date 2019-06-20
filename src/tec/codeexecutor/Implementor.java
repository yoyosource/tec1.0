package tec.codeexecutor;

import tec.interfaces.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Implementor {

    private ArrayList<Statement> statements = new ArrayList<>();

    public void add(Statement statement) {
        statements.add(statement);
    }

    public void add(Statement... statements) {
        this.statements.addAll(Arrays.stream(statements).collect(Collectors.toList()));
    }

    public ArrayList<Statement> get() {
        return statements;
    }

}
