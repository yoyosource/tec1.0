package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class print implements Statement {
	@Override
	public String getName() {
		return "print";
	}

	@Override
	public boolean execute(ArrayList<Token> tokens, Executor executor) {

		return false;
	}
}
