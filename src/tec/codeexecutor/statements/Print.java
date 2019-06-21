package tec.codeexecutor.statements;

import tec.calculator.Calculator;
import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public class Print implements Statement {

	@Override
	public String getName() {
		return "print";
	}

	@Override
	public boolean execute(ArrayList<Token> tokens, Executor executor) {

		Expression expression = new Expression(tokens);
		expression.build();
		String s = expression.getString();
		System.out.println(s);

		return false;
	}

}
