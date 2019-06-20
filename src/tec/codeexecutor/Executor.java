package tec.codeexecutor;

import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class Executor {

	private int index = 0;
	private Implementor implementor;
	private ArrayList<Token> tokens;

	public Executor(ArrayList<Token> tokens, Implementor implementor) {
		this.implementor = implementor;
		this.tokens = tokens;
	}

	public void run() {
		while (index < tokens.size()) {
			if (isStatement()) {
				runStatement();
			}

			index++;
		}
	}

	public void jump(int index) {

	}

	private boolean isStatement() {
		if (index != 0) {

		} else if (tokens.get(index - 1).getKey().equals("NNN")) {

		} else {
			return false;
		}
		for (Statement statement : implementor.get()) {
			if (statement.getName().equals(tokens.get(index))) {
				return true;
			}
		}
		return false;
	}

	private boolean runStatement() {
		for (Statement statement : implementor.get()) {
			if (statement.getName().equals(tokens.get(index))) {
				return statement.execute(getTokensToNextLine(), this);
			}
		}
		return false;
	}

	private ArrayList<Token> getTokensToNextLine() {
		ArrayList<Token> tokens = new ArrayList<>();
		for (int i = index + 1; i < tokens.size(); i++) {
			if (tokens.get(i).getKey().equals("NNN")) {
				break;
			}
			tokens.add(tokens.get(i));
		}
		return tokens;
	}
}
