package tec.codeexecutor;

import tec.interfaces.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type Implementor.
 */
public class Implementor {

    private ArrayList<Statement> statements = new ArrayList<>();

	/**
	 * Add.
	 *
	 * @param statement the statement
	 */
	public void add(Statement statement) {
        statements.add(statement);
    }

	/**
	 * Add.
	 *
	 * @param statements the statements
	 */
	public void add(Statement... statements) {
        this.statements.addAll(Arrays.stream(statements).collect(Collectors.toList()));
    }

	/**
	 * Get array list.
	 *
	 * @return the array list
	 */
	public ArrayList<Statement> get() {
        return statements;
    }

}
