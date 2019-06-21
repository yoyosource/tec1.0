package tec.interfaces;

import tec.codeexecutor.VariableState;
import tec.utils.Token;

import java.util.ArrayList;

/**
 * The interface Statement.
 */
public interface Statement {
	/**
	 * Returns the name of the statement.
	 * This is also the identifier for using this statement.
	 *
	 * @return name of statement.
	 */
	String getName();

	/**
	 * Tries to execute the statement, with a list of tokens.
	 * If it is successful, it will return {@code true}.
	 * Else, it will return {@code false}
	 *
	 * @param tokens        the array of tokens passed to statement
	 * @param variableState the variable state
	 * @return the boolean
	 */
	boolean execute(ArrayList<Token> tokens, VariableState variableState);

}
