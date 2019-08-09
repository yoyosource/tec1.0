package tec.codeexecutor;

import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private ArrayList<Integer> jumpBackPointFunction = new ArrayList<>();
    private ArrayList<Integer> triggerJumpBackFunction = new ArrayList<>();
    private ArrayList<String> currentReturnType = new ArrayList<>();
    private HashMap<String, Integer> funcPoints = new HashMap<>();
    private HashMap<String, String> returnType = new HashMap<>();

    private Token returnSystem = null;

    private String error = "";

    private String groundPath = "";

    public String getGroundPath() {
        return groundPath;
    }

    public void setGroundPath(String groundPath) {
        this.groundPath = groundPath;
    }

    public void setFuncReturn(Token returnSystem) {
		this.returnSystem = returnSystem;
	}

	public Token getFuncReturn() {
		return returnSystem;
	}

	public void setError(String error) {
        this.error = error;
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
				in = i - 1;
				break;
			}
        }

	    index = in;
	}

    public boolean runExpressionInfo(Expression expression) {
        if (expression.getObject() == null) {
            System.out.println("Token: " + index);
            System.out.println("ERROR: " + expression.getError());
            return false;
        }
        return true;
    }

    private void funcJumpPoints() {
		ArrayList<Integer> indicies = new ArrayList<>();

		int i = 0;

		while (i < tokens.size()) {
			if (tokens.get(i).getKey().equals("COD")  && tokens.get(i).getVal().equals("func")) {
				indicies.add(i);
			}
			i = getLineEnd(i) + 1;
		}

		running = true;

		if (indicies.isEmpty()) {
			return;
		}

		for (int index : indicies) {
			String[] strings = getFuncHead(index);
			String s = strings[0].trim();
			if (!s.isEmpty()) {
				if (!funcPoints.containsKey(s)) {
					funcPoints.put(s, getLineEnd(index));
					if (strings.length == 2 && !strings[1].trim().isEmpty()) {
						returnType.put(s, strings[1].trim());
					} else {
						returnType.put(s, "");
					}
				}
			}
		}
	}

	private String[] getFuncHead(int index) {
		int[] ints = getBlockRange(index);

		ArrayList<Token> head = getRange(index, ints[0] - 1);

		if (!(head.get(0).getKey().equals("COD") && head.get(0).getVal().equals("func"))) {
			return new String[0];
		}
		head.remove(0);

		if (!(head.get(head.size() - 1).getKey().equals("BLb") && head.get(head.size() - 1).getVal().equals("{"))) {
			return new String[0];
		}
		head.remove(head.size() - 1);

		StringBuilder st = new StringBuilder();
		if (!head.get(0).getKey().equals("COD")) {
			return new String[0];
		}
		st.append(head.get(0).getVal().toString());
		st.append(": ");
		head.remove(0);

		boolean b = false;
		Token t = null;

		for (Token token : head) {
			if (b) {
				t = token;
				break;
			}
			if (token.getKey().equals("RET")) {
				b = true;
				continue;
			}
			if (token.getKey().equals("typ")) {
				st.append(token.getVal().toString());
				st.append(" ");
			}
		}

		if (t == null) {
			return new String[]{st.toString()};
		}

		return new String[]{st.toString(), t.getVal().toString()};
	}

	/**
	 * Run.
	 */
	public void run() {
		funcJumpPoints();

		variableStateStack.add(new VariableState());
		triggerVariableStackRemove.add(tokens.size() + 1);
        triggerJumpBack.add(tokens.size() + 1);
        triggerJumpBackFunction.add(tokens.size() + 1);

		while (running && index < tokens.size()) {
			if (isFunction()) {
				runFunction();
			} else if (isStatement()) {
				runStatement();
				jumpToLineEnd();
			} else if (isVariable()) {
			    runVariable();
				jumpToLineEnd();
			}

			index++;

			if (index == triggerJumpBackFunction.get(triggerJumpBackFunction.size() - 1)) {
				jumpBackFunc(null);
				jumpToLineEnd();
			}
			if (index == triggerVariableStackRemove.get(triggerVariableStackRemove.size() - 1)) {
				triggerVariableStackRemove.remove(triggerVariableStackRemove.size() - 1);
				deleteLastVariableState();
			}
			if (index == triggerJumpBack.get(triggerJumpBack.size() - 1)) {
                triggerJumpBack.remove(triggerJumpBack.size() - 1);
                jumpBackBlock();
                jumpBackStatement();
            }
		}
	}

	public void jumpBackFunc(String type) {

		if (triggerJumpBackFunction.size() == 1) {
			running = false;
			return;
		}

		String t = currentReturnType.get(currentReturnType.size() - 1);

		if (type == null && t.length() == 0) {

		} else if (type != null && type.equals(t)) {

		} else if (type != null && t.equals("any")) {

		} else {
			running = false;
			return;
		}

		running = true;

		if (type == null) {
			returnSystem = null;
		} else {
			returnSystem = new Token(t, type);
		}

		currentReturnType.remove(currentReturnType.size() - 1);
		triggerJumpBackFunction.remove(triggerJumpBackFunction.size() - 1);
		deleteLastFunctionVariabelState();
		index = jumpBackPointFunction.get(jumpBackPointFunction.size() - 1);
		jumpBackPointFunction.remove(jumpBackPointFunction.size() - 1);
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

	private int jumpBackStatement(int index) {
		int in = index;

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

		return in;
	}

    /**
     * Add a new Trigger Jump Back
     * @param triggerIndex Index where to trigger the jump Back
     */
    public void addJumpBackTrigger(int triggerIndex) {
        triggerJumpBack.add(triggerIndex);
    }

    /**
     * Add a new Variable Remove Index
     * @param triggerIndex Index where to remove the Varibale State
     */
	public void addVariableStateRemoveTrigger(int triggerIndex) {
		triggerVariableStackRemove.add(triggerIndex);
	}

	/**
	 * Create new Variable State with previous Variables
	 */
	public void createVariableStateFromPrevious() {
		ArrayList<Var> vars = variableStateStack.lastElement().getVars();
		VariableState variableState = new VariableState(true);
		for (Var var : vars) {
			variableState.addVar(var);
		}
		variableStateStack.add(variableState);
	}

	/**
	 * Create new Variable State without previous Variables
	 */
	public void createNewVariableState() {
		VariableState variableState = new VariableState();
		for (Var var : variableStateStack.firstElement().getVars()) {
			String varName = var.getName();
			if (varName.matches("[A-Z][A-Z_\\-]+")) {
				variableState.addVar(var);
			}
		}
		variableStateStack.add(variableState);
	}

	/**
	 * Removes the last Variable State
	 */
	public void deleteLastVariableState() {
		if (variableStateStack.size() > 1) {
			variableStateStack.pop();
		}
	}

	public void deleteLastFunctionVariabelState() {
		while (variableStateStack.size() > 1 && variableStateStack.lastElement().isDerived()) {
			variableStateStack.pop();
		}
		if (variableStateStack.size() > 1) {
			variableStateStack.pop();
		}
	}

	/**
	 * Get line End index
	 */
	public int getLineEnd(int index) {
		if (tokens.get(index).getKey().equals("NNN")) {
			return index;
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
		return index;
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
		if (tokens.get(index + 1).getKey().equals("OPE")) {
			if (tokens.get(index + 2).getKey().equals("OPE") && tokens.get(index + 1).getVal().equals("+") && tokens.get(index + 2).getVal().equals("+")) {
				String name = tokens.get(index).getVal().toString();

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) + 1, variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) + 1, variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
			if (tokens.get(index + 2).getKey().equals("OPE") && tokens.get(index + 1).getVal().equals("-") && tokens.get(index + 2).getVal().equals("-")) {
				String name = tokens.get(index).getVal().toString();

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) - 1, variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) - 1, variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
			if (tokens.get(index + 2).getKey().equals("ASG") && tokens.get(index + 1).getVal().equals("+")) {
				String name = tokens.get(index).getVal().toString();
				index += 2;

				ArrayList<Token> tokens = getTokensToNextLine();
				Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
				expression.build();

				if (!runExpressionInfo(expression)) {
					return false;
				}

				if (!expression.getType().equals("num") && !expression.getType().equals("int")) {
					return false;
				}

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) + (int)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) + (float)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
			if (tokens.get(index + 2).getKey().equals("ASG") && tokens.get(index + 1).getVal().equals("-")) {
				String name = tokens.get(index).getVal().toString();
				index += 2;

				ArrayList<Token> tokens = getTokensToNextLine();
				Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
				expression.build();

				if (!runExpressionInfo(expression)) {
					return false;
				}

				if (!expression.getType().equals("num") && !expression.getType().equals("int")) {
					return false;
				}

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) - (int)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) - (float)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
			if (tokens.get(index + 2).getKey().equals("ASG") && tokens.get(index + 1).getVal().equals("*")) {
				String name = tokens.get(index).getVal().toString();
				index += 2;

				ArrayList<Token> tokens = getTokensToNextLine();
				Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
				expression.build();

				if (!runExpressionInfo(expression)) {
					return false;
				}

				if (!expression.getType().equals("num") && !expression.getType().equals("int")) {
					return false;
				}

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) * (int)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) * (float)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
			if (tokens.get(index + 2).getKey().equals("ASG") && tokens.get(index + 1).getVal().equals("/")) {
				String name = tokens.get(index).getVal().toString();
				index += 2;

				ArrayList<Token> tokens = getTokensToNextLine();
				Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
				expression.build();

				if (!runExpressionInfo(expression)) {
					return false;
				}

				if (!expression.getType().equals("num") && !expression.getType().equals("int")) {
					return false;
				}

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) / (int)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) / (float)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}

			if (tokens.get(index + 2).getKey().equals("ASG") && tokens.get(index + 1).getVal().equals("^")) {
				String name = tokens.get(index).getVal().toString();
				index += 2;

				ArrayList<Token> tokens = getTokensToNextLine();
				Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
				expression.build();

				if (!runExpressionInfo(expression)) {
					return false;
				}

				if (!expression.getType().equals("num") && !expression.getType().equals("int")) {
					return false;
				}

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)Math.pow((int)variableState.getVarValue(name), (int)expression.getObject()), variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)Math.pow((float)variableState.getVarValue(name), (float)expression.getObject()), variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
			if (tokens.get(index + 2).getKey().equals("ASG") && tokens.get(index + 1).getVal().equals("%")) {
				String name = tokens.get(index).getVal().toString();
				index += 2;

				ArrayList<Token> tokens = getTokensToNextLine();
				Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
				expression.build();

				if (!runExpressionInfo(expression)) {
					return false;
				}

				if (!expression.getType().equals("num") && !expression.getType().equals("int")) {
					return false;
				}

				if (!variableStateStack.lastElement().getVarType(name).equals("int") && !variableStateStack.lastElement().getVarType(name).equals("num")) {
					return false;
				}

				VariableState variableState = variableStateStack.lastElement();

				if (variableState.getVarType(name).equals("int")) {
					if (variableState.setVar(name, new Var(name, (int)variableState.getVarValue(name) % (int)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				} else {
					if (variableState.setVar(name, new Var(name, (float)variableState.getVarValue(name) % (float)expression.getObject(), variableState.getVarType(name)))) {
						return true;
					}
				}
				return false;
			}
		}
		if (tokens.get(index + 1).getKey().equals("ASG")) {

			String name = tokens.get(index).getVal().toString();
			index++;

			ArrayList<Token> tokens = getTokensToNextLine();
			Expression expression = new Expression(tokens, variableStateStack.lastElement(), this);
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
		tokens.add(0, this.tokens.get(index));

		if (tokens.size() == 0) {
			return false;
		}

		if (!tokens.get(0).getKey().equals("COD")) {
			return false;
		}

		tokens.remove(0);

		if (tokens.isEmpty()) {
			return false;
		}

		if (!(tokens.get(0).getKey().equals("STb") && tokens.get(tokens.size() - 1).getKey().equals("STb"))) {
			return false;
		}

		if (!(tokens.get(0).getVal().equals("(") && tokens.get(tokens.size() - 1).getVal().equals(")"))) {
			return false;
		}

		return true;
	}

	public boolean runFunction() {
		return runFunction(tokens.get(index).getVal().toString(), getTokensToNextLine());
	}

	public boolean runFunction(String funcName, ArrayList<Token> tokens) {
		if (tokens.get(0).getKey().equals("STb") && tokens.get(tokens.size() - 1).getKey().equals("STb")) {
			tokens.remove(0);
			tokens.remove(tokens.size() - 1);
		}

		ArrayList<ArrayList<Token>> tokenExpressions = new ArrayList<>();

		if (tokens.size() > 0) {
			ArrayList<Token> cTokens = new ArrayList<>();

			int i = 0;
			int l = 0;
			for (Token token : tokens) {
				if (token.getKey().equals("SEP") && token.getVal().equals(",")) {
					tokenExpressions.add(cTokens);
					cTokens = new ArrayList<>();
					l = i;
				} else {
					cTokens.add(token);
				}
				i++;
			}
			if (l != tokens.size()) {
				tokenExpressions.add(cTokens);
			}
		}

		ArrayList<Object> parameter = new ArrayList<>();
		ArrayList<String> type = new ArrayList<>();

		for (ArrayList<Token> tokens1 : tokenExpressions) {
			Expression expression = new Expression(tokens1, variableStateStack.lastElement(), this);
			expression.build();

			if (!runExpressionInfo(expression)) {
				return false;
			}

			parameter.add(expression.getObject());
			type.add(expression.getType());
		}

		StringBuilder st = new StringBuilder();
		st.append(funcName);
		st.append(": ");

		for (String s : type) {
			st.append(s);
			st.append(" ");
		}

		String s = st.toString().trim();

		if (funcPoints.containsKey(s)) {
			int nIndex = funcPoints.get(s);

			String t = returnType.get(s);
			currentReturnType.add(t);

			jumpBackPointFunction.add(getLineEnd(index));
			int[] ints = getBlockRange(nIndex - 1);
			triggerJumpBackFunction.add(ints[1]);
			createNewVariableState();

			int parameterindex = jumpBackStatement(nIndex);
			ArrayList<Token> parameterVarNames = getRange(parameterindex + 1, nIndex - 2);
			ArrayList<String> varNames = new ArrayList<>();

			boolean b = false;
			for (Token token : parameterVarNames) {
				if (b) {
					varNames.add(token.getVal().toString());
					b = false;
					continue;
				}
				if (token.getKey().equals("typ")) {
					b = true;
				}
			}

			for (int i = 0; i < type.size(); i++) {
				Var var = new Var(varNames.get(i), parameter.get(i), type.get(i));
				variableStateStack.lastElement().addVar(var);
			}

			index = nIndex;
			directExecuteFunction();

			return true;
		}

		return false;
	}

	private void directExecuteFunction() {
	    int finish = variableStateStack.size() - 1;

        running = true;

        while (running && index < tokens.size()) {
            if (isFunction()) {
                runFunction();
            } else if (isStatement()) {
                runStatement();
                jumpToLineEnd();
            } else if (isVariable()) {
                runVariable();
                jumpToLineEnd();
            }

            index++;

            if (index == triggerJumpBackFunction.get(triggerJumpBackFunction.size() - 1)) {
                jumpBackFunc(null);
                jumpToLineEnd();
            }
            if (index == triggerVariableStackRemove.get(0)) {
                triggerVariableStackRemove.remove(0);
                deleteLastVariableState();
            }
            if (index == triggerJumpBack.get(0)) {
                triggerJumpBack.remove(0);
                jumpBackBlock();
                jumpBackStatement();
            }

            if (variableStateStack.size() == finish) {
                index--;
                return;
            }
        }
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
				return new int[2];
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

	public int getClosingBlock(int start) {
		int in = start;
		while (in < tokens.size()) {
			if (tokens.get(in).getKey().equals("NNN")) {
				return start;
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
