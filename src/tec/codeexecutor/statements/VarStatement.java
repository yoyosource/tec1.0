package tec.codeexecutor.statements;

import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class VarStatement implements Statement {

    @Override
    public String getName() {
        return "var";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState) {
        return false;
    }
}
