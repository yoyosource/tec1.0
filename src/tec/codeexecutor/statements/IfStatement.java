package tec.codeexecutor.statements;

import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

/**
 * The type If statement.
 */
public class IfStatement implements Statement {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState) {
        return false;
    }
}
