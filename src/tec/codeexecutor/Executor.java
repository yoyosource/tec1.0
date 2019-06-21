package tec.codeexecutor;

import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Stack;

/**
 * The type Executor.
 */
public class Executor {

	private int index = 0;
	private Implementor implementor;
	private ArrayList<Token> tokens;

	private Stack<VariableState> variableStateStack = new Stack<>();

	/**
	 * Instantiates a new Executor.
	 *
	 * @param tokens      the tokens
	 * @param implementor the implementor
	 */
	public Executor(ArrayList<Token> tokens, Implementor implementor) {
		this.implementor = implementor;
		this.tokens = tokens;
	}

	/**
	 * Run.
	 */
	public void run() {
		variableStateStack.add(new VariableState());
		while (index < tokens.size()) {
			if (isStatement()) {
				runStatement();
			}

			index++;
		}
	}

	/**
	 * Jump.
	 *
	 * @param index the index
	 */
	public void jump(int index) {

	}

	private boolean isStatement() {
		if (index == 0) {

		} else if (tokens.get(index - 1).getKey().equals("NNN")) {

		} else {
			return false;
		}
		for (Statement statement : implementor.get()) {
			if (statement.getName().equals(tokens.get(index).getVal())) {
				return true;
			}
		}
		return false;
	}

	private boolean runStatement() {
		for (Statement statement : implementor.get()) {
			if (statement.getName().equals(tokens.get(index).getVal())) {
				return statement.execute(getTokensToNextLine(), variableStateStack.lastElement());
			}
		}
		return false;
	}

	private ArrayList<Token> getTokensToNextLine() {
		ArrayList<Token> tokens = new ArrayList<>();
		for (int i = index + 1; i < this.tokens.size(); i++) {
			if (this.tokens.get(i).getKey().equals("NNN")) {
				break;
			}
			tokens.add(this.tokens.get(i));
		}
		return tokens;
	}

	/**
	 * End execution.
	 */
	protected void endExecution() {
		index = tokens.size();
	}
}
