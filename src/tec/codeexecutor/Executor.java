package tec.codeexecutor;

import tec.Tec;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Stack;

public class Executor {

	private static int index = 0;
	private boolean running = true;
	private Implementor implementor;
	private static ArrayList<Token> tokens;

	private Stack<VariableState> variableStateStack = new Stack<>();

	public Executor(ArrayList<Token> tokens, Implementor implementor) {
		this.implementor = implementor;
		this.tokens = tokens;
	}

	public static void incrementIndex() {
		index++;
	}

	public static void jumpToOpeningBracket() {
		for (int i = index; i < tokens.size(); i++) {
			if (tokens.get(index).getKey().equals("BLb") && tokens.get(index).getVal().equals("{")) {
				index = i;
				break;
			}
		}
	}

	public static void jumpToClosingBracket() {
		int brCount = 0;
		boolean st = false;
		while (!st && (brCount != 0 || index < tokens.size())) {
			if (tokens.get(index).getKey().equals("STb") && tokens.get(index).getVal().toString().equals("(")) {
				brCount++;
				st = true;
			}
			if (tokens.get(index).getKey().equals("STb") && tokens.get(index).getVal().toString().equals(")")) {
				brCount--;
			}
			index++;
		}
	}

	public static boolean runExpressionInfo(Expression expression) {
		if (Tec.debug > 0) {
			System.out.println("Expression " + Tec.expressions + " build time: " + expression.getExpressionTime() + "ms");
			Tec.expressions += 1;
			if (Tec.debug == 2) {
				System.out.println(expression.toString());
			}
			if (Tec.debug > 1) {
				System.out.println(expression.advancedInfo().toString());
			}
		}
		if (expression.getObject() == null) {
			System.out.println("ERROR: " + expression.getError());
			return false;
		}
		return true;
	}

	public void run() {
		variableStateStack.add(new VariableState());
		while (running && index < tokens.size()) {
			if (isStatement()) {
				System.out.println(" " + tokens.get(index));
				runStatement();
				jumpToLineEnd();
			} else if (isVariable()) {
				System.out.println(">" + tokens.get(index));
				runVariable();
				jumpToLineEnd();
			} else {
				System.out.println("-" + tokens.get(index));
			}


			index++;
		}
	}

	public void jumpToLineEnd() {
		for (int i = index + 1; i < this.tokens.size(); i++) {
			if (this.tokens.get(i).getKey().equals("NNN")) {
				index = i;
				break;
			}
			if (i == tokens.size() - 1) {
				running = false;
			}
		}
	}

	private boolean isStatement() {
		if (index == 0) {

		} else if (tokens.get(index - 1).getKey().equals("NNN")) {

		} else if (tokens.get(index - 1).getKey().equals("BLb") && tokens.get(index - 1).getVal().equals("}")) {

		}
		else {
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

	private boolean isVariable() {
		if (variableStateStack.lastElement().isVariable(tokens.get(index).getVal().toString())) {
			return true;
		}
		return false;
	}

	private boolean runVariable() {
		if (tokens.get(index + 1).getKey().equals("ASG")) {

			String name = tokens.get(index).getVal().toString();
			index++;

			ArrayList<Token> tokens = getTokensToNextLine();
			Expression expression = new Expression(tokens);
			expression.build();

			if (!runExpressionInfo(expression)) {
				return false;
			}

			if (variableStateStack.lastElement().setVar(name, new Var(name, expression.getObject(), expression.getType()))) {
				return true;
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

	protected void endExecution() {
		running = false;
	}
}
