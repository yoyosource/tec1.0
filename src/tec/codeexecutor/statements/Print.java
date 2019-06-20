package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class Print implements Statement {
	@Override
	public String getName() {
		return "print";
	}

	@Override
	public boolean execute(ArrayList<Token> tokens, Executor executor) {
		ArrayList<Token> calculate;
		ArrayList<Boolean> calculateHistory;
		for (int i = 0; i < tokens.size(); i++ ) {
			Token token = tokens.get(i);
			String key = token.getKey();
			if (key.equals("int") || key.equals("num") || token.getKey().equals("OPE")) {
				if (key.equals("OPE")) {
					
				} else {

				}
			}
		}
		return false;
	}
	private int calculate(ArrayList<Token> tokens) {
		return 0;
	}
}
