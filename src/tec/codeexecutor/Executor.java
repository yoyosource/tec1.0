package tec.codeexecutor;

import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * The type Executor.
 */
@SuppressWarnings("ALL")
public class Executor {

	private int index = 0;
	private boolean running = true;
	private Implementor implementor;
	private ArrayList<Token> tokens;

	private Stack<VariableState> variableStateStack = new Stack<>();

	private ArrayList<Integer> triggerVariableStackRemove = new ArrayList<>();
    private ArrayList<Integer> triggerJumpBack = new ArrayList<>();

	public int getIndex() {
		return index;
	}

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

	public void incrementIndex() {
		index++;
	}

	public void jumpToIndex(int in) {
		index = in;
	}

	public void jumpToOpeningBracket() {
		for (int i = index; i < tokens.size(); i++) {
			if (tokens.get(index).getKey().equals("BLb") && tokens.get(index).getVal().equals("{")) {
				index = i;
				break;
			}
		}
	}

	public void jumpToClosingBracket() {
	    int bracket = 0;
	    boolean b = false;
	    int in = index;

	    for (int i = in; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("BLb") && tokens.get(i).getVal().toString().equals("{")) {
                if (!b) {
                    b = true;
                }
                bracket++;
            }
            if (tokens.get(i).getKey().equals("BLb") && tokens.get(i).getVal().toString().equals("}")) {
                bracket--;
            }
			if (b && bracket == 0) {
				in = i;
				break;
			}
        }

	    index = in;
	}

    public boolean runExpressionInfo(Expression expression) {
        if (expression.getObject() == null) {
            System.out.println(index);
            System.out.println("ERROR: " + expression.getError());
            return false;
        }
        return true;
    }

	/**
	 * Run.
	 */
	public void run() {
		variableStateStack.add(new VariableState());
		triggerVariableStackRemove.add(tokens.size() + 1);
        triggerJumpBack.add(tokens.size() + 1);

		while (running && index < tokens.size()) {
			if (isStatement()) {
				runStatement();
				jumpToLineEnd();
			} else if (isVariable()) {
			    runVariable();
				jumpToLineEnd();
			}

			index++;

			if (index == triggerVariableStackRemove.get(0)) {
				triggerVariableStackRemove.remove(0);
				deleteLastVariableState();
			}
			if (index == triggerJumpBack.get(0)) {
                triggerJumpBack.remove(0);
                jumpBackBlock();
                jumpBackStatement();
            }
		}
	}

	private void jumpBackBlock() {
	    int in = this.index;

	    boolean b = false;
	    int brackets = 0;
	    for (int i = index; i >= 0; i--) {
	        if (b && brackets == 0) {
	            in = i;
	            break;
            }
	        if (tokens.get(i).getKey().equals("BLb") && tokens.get(i).getVal().equals("}")) {
	            if (!b) {
	                b = true;
                }
	            brackets++;
            }
            if (tokens.get(i).getKey().equals("BLb") && tokens.get(i).getVal().equals("{")) {
                brackets--;
            }
        }

	    this.index = in;
    }

    private void jumpBackStatement() {
        int in = this.index;

        boolean b = false;
        int brackets = 0;
        for (int i = index; i >= 0; i--) {
            if (b && brackets == 0) {
                in = i;
                break;
            }
            if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals(")")) {
                if (!b) {
                    b = true;
                }
                brackets++;
            }
            if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("(")) {
                brackets--;
            }
        }

        this.index = in;
    }

    /**
     * Add a new Trigger Jump Back
     * @param triggerIndex Index where to trigger the jump Back
     */
    public void addJumpBackTrigger(int triggerIndex) {
        triggerJumpBack.add(triggerIndex);
        Collections.sort(triggerJumpBack);
    }

    /**
     * Add a new Variable Remove Index
     * @param triggerIndex Index where to remove the Varibale State
     */
	public void addVariableStateRemoveTrigger(int triggerIndex) {
		triggerVariableStackRemove.add(triggerIndex);
		Collections.sort(triggerVariableStackRemove);
	}

	/**
	 * Create new Variable State with previous Variables
	 */
	public void createVariableStateFromPrevious() {
		ArrayList<Var> vars = variableStateStack.lastElement().getVars();
		VariableState variableState = new VariableState();
		for (Var var : vars) {
			variableState.addVar(var);
		}
		variableStateStack.add(variableState);
	}

	/**
	 * Create new Variable State without previous Variables
	 */
	public void createNewVariableState() {
		variableStateStack.add(new VariableState());
	}

	/**
	 * Removes the last Variable State
	 */
	public void deleteLastVariableState() {
		if (variableStateStack.size() > 1) {
			variableStateStack.pop();
		}
	}

	/**
	 * Jump to line end.
	 */
	public void jumpToLineEnd() {
		if (tokens.get(index).getKey().equals("NNN")) {
			return;
		}
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
				return statement.execute(getTokensToNextLine(), variableStateStack.lastElement(), this);
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
			Expression expression = new Expression(tokens, variableStateStack.lastElement());
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

	private boolean isFunction() {
		ArrayList<Token> tokens = getTokensToNextLine();

		if (!tokens.get(0).getKey().equals("COD")) {
			return false;
		}

		tokens.remove(0);

		if (!(tokens.get(0).getKey().equals("STb") && tokens.get(tokens.size() - 1).getKey().equals("STb"))) {
			return false;
		}

		if (!(tokens.get(0).getVal().equals("(") && tokens.get(tokens.size() - 1).getVal().equals(")"))) {
			return false;
		}

		return true;
	}

	private boolean runFunction() {
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

	public int[] getBlockRange() {
		return getBlockRange(index);
	}

	public int[] getBlockRange(int index) {
		int in = index;
		while (in < tokens.size()) {
			if (tokens.get(in).getKey().equals("NNN")) {
				return null;
			}
			if (tokens.get(in).getKey().equals("BLb") && tokens.get(in).getVal().equals("{")) {
				break;
			}
			in++;
		}

		in++;
		int bracket = 1;
		int ou = in;
		for (int i = in; i < tokens.size(); i++) {
			if (tokens.get(i).getKey().equals("BLb") && tokens.get(i).getVal().equals("{")) {
				bracket++;
			}
			if (tokens.get(i).getKey().equals("BLb") && tokens.get(i).getVal().equals("}")) {
				bracket--;
			}
			if (bracket == 0) {
				ou = i;
				break;
			}
		}

		return new int[]{in, ou};
	}

	public int[] getIfRange() {
		boolean gotElse = false;
		boolean isDone = false;

		if (!(tokens.get(index).getKey().equals("COD") && tokens.get(index).getVal().equals("if"))) {
			return null;
		}

		int in = index;

		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(in + 1);

		while (!isDone) {

			int[] ints = getBlockRange(in);
			if (ints[1] != tokens.size() - 1) {
				ints[1] = ints[1] + 1;
			} else {
				in = getBlockRange(in)[1];
				break;
			}

			if (gotElse) {
				break;
			}

			if (tokens.get(ints[1]).getKey().equals("COD") && tokens.get(ints[1]).getVal().equals("else")) {
				if (ints[1] != tokens.size() - 1) {
					ints[1] = ints[1] + 1;
				}

				if (!(tokens.get(ints[1]).getKey().equals("COD") && tokens.get(ints[1]).getVal().equals("if"))) {
					gotElse = true;
				} else {
					indices.add(ints[1] + 1);
				}
				in = ints[1];
			} else {
				isDone = true;
			}

		}

		ArrayList<Integer> indices2 = new ArrayList<>();

		for (int i = 0; i < indices.size(); i++) {
			indices2.add(getClosingBracket(indices.get(i)));
		}

		indices.add(in);
		indices2.add(in);

		ArrayList<Integer> ranges = new ArrayList<>();
		for (int i = 0; i < indices.size(); i++) {
			ranges.add(indices.get(i));
			ranges.add(indices2.get(i));
		}

		int[] ints = new int[ranges.size()];
		for (int i = 0; i < ranges.size(); i++) {
			ints[i] = ranges.get(i);
		}

		return ints;
	}

	public int getClosingBracket(int start) {
		if (!(tokens.get(start).getKey().equals("STb") && tokens.get(start).getVal().equals("("))) {
			return start;
		}
		int bracket = 1;
		int ou = start;
		start++;
		for (int i = start; i < tokens.size(); i++) {
			if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("(")) {
				bracket++;
			}
			if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals(")")) {
				bracket--;
			}
			if (bracket == 0) {
				ou = i;
				break;
			}
		}
		return ou;
	}

	public ArrayList<Token> getRange(int start, int stop) {
		int max = Math.max(start, stop);
		int min = Math.min(start, stop);

		ArrayList<Token> nTokens = new ArrayList<>();

		for (int i = min; i <= max; i++) {
			nTokens.add(tokens.get(i));
		}

		return nTokens;
	}

	/**
	 * End execution.
	 */
	protected void endExecution() {
		running = false;
	}
}
