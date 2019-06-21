package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
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

		Expression expression = new Expression(tokens);
		expression.build();
		System.out.println("Expression build time: " + expression.getExpressionTime() + "ms");
		if (expression.getString() == null) {
			System.out.println("ERROR: " + expression.getError());
			return false;
		}
		String s = expression.getString();
		System.out.println(s);

		return true;
	}

}
