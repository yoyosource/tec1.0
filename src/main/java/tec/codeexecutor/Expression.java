package tec.codeexecutor;

import tec.utils.Token;

import java.util.ArrayList;
import java.util.List;

public class Expression {

    private List<Token> tokens;
    private VariableState variableState;
    private Executor executor;

    private ExpressionState expressionState;

    /**
     * Instantiates a new expression.
     *
     * @param tokens The tokens associated with this ExpressionOld.
     * @param variableState The tokens associated with this ExpressionOld.
     * @param executor The executor to execute functions.
     */
    public Expression(List<Token> tokens, VariableState variableState, Executor executor) {
        this.tokens = tokens;
        this.variableState = variableState;
        this.executor = executor;

        this.expressionState = new ExpressionState();
    }

    /**
     * Instantiates a new expression.
     *
     * @param tokens The tokens associated with this ExpressionOld.
     * @param variableState The tokens associated with this ExpressionOld.
     */
    public Expression(List<Token> tokens, VariableState variableState) {
        this.tokens = tokens;
        this.variableState = variableState;
        this.executor = null;

        this.expressionState = new ExpressionState();
    }

    /**
     * Instantiates a new expression.
     *
     * @param tokens The tokens associated with this ExpressionOld.
     * @param executor The executor to execute functions.
     */
    public Expression(List<Token> tokens, Executor executor) {
        this.tokens = tokens;
        this.variableState = null;
        this.executor = executor;

        this.expressionState = new ExpressionState();
    }

    /**
     * Instantiates a new expression.
     *
     * @param tokens The tokens associated with this ExpressionOld.
     */
    public Expression(List<Token> tokens) {
        this.tokens = tokens;
        this.variableState = null;
        this.executor = null;

        this.expressionState = new ExpressionState();
    }

    /**
     * Evaluate the expression to a single value.
     */
    public void build() {
        long time = System.currentTimeMillis();

        replaceFunctions();
        replaceVariables();

        specialFunctions();

        ExpressionUtils.removeBrackets(tokens);

        specialFunctions();
        replaceVariables();

        ExpressionUtils.removeBrackets(tokens);

        if (ExpressionUtils.isCalculation(tokens)) {
            Calculator calculator = new Calculator(tokens);
            Object output = calculator.calculate();

            Token t = ExpressionUtils.getToken(output);

            expressionState.setResult(t);
            expressionState.addTime(System.currentTimeMillis() - time);
            return;
        }

        if (tokens.size() == 1) {
            expressionState.setResult(tokens.get(0));
            expressionState.addTime(System.currentTimeMillis() - time);
            return;
        }

        ExpressionUtils.removeBrackets(tokens);

        try {
            buildOther();
        } catch (Exception e) {
            expressionState.addError("An unexpected Error occurred.");
            System.out.println(tokens);
            e.printStackTrace();
        }
        expressionState.addTime(System.currentTimeMillis() - time);
    }

    /**
     * Get the Result of the expression.
     */
    public ExpressionState getResult() {
        return expressionState;
    }

    private void replaceFunctions() {
        if (executor == null) {
            return;
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (i > 1 && tokens.get(i - 1).getKey().equals("SEP")) {
                continue;
            }

            if (!token.getKey().equals("COD")) {
                continue;
            }

            if (i < tokens.size() - 1 && !(tokens.get(i + 1).getKey().equals("STb") && tokens.get(i + 1).getVal().equals("("))) {
                continue;
            }

            int j = i + 1;
            if (j >= tokens.size()) {
                continue;
            }

            int c = ExpressionUtils.getClosingBracket(j, tokens);
            List<Token> func = ExpressionUtils.getRange(i, c, this.tokens);

            String funcName = func.get(0).getVal().toString();
            func.remove(0);

            try {
                boolean b = executor.runFunction(funcName, func);
                Token t = executor.getFuncReturn();

                if (b) {
                    for (int k = c; k > i; k--) {
                        tokens.remove(k);
                    }
                    tokens.set(i, t);
                } else {
                    String pointer = ExpressionUtils.getPointer(tokens, i);
                    expressionState.addError("The Function '" + funcName + "()' was not found\n" + pointer);
                }
            } catch (Exception e) {
                String pointer = ExpressionUtils.getPointer(tokens, i);
                expressionState.addError("An unexpected Error occurred while executing the function '" + funcName + "()'\n" + pointer);
            }
        }
    }

    private void replaceVariables() {
        if (variableState == null) {
            return;
        }

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (i > 1 && tokens.get(i - 1).getKey().equals("SEP")) {
                continue;
            }

            if (i < tokens.size() - 2 && tokens.get(i + 1).getKey().equals("STb") && tokens.get(i + 1).getVal().equals("(")) {
                continue;
            }

            if (token.getKey().equals("COD")) {
                if (variableState.isVariable(tokens.get(i).getVal().toString())) {

                    Var v = variableState.getVar(tokens.get(i).getVal().toString());

                    if (v.isArray()) {
                        if (!(v.getValue() instanceof ArrayList)) {
                            String pointer = ExpressionUtils.getPointer(tokens, i);
                            expressionState.addError("The Variable '" + token.getVal() + "' is not of type Array\n" + pointer);
                            continue;
                        }
                        List<Object> array = v.getValueAsArray();

                        if (i < tokens.size() - 3 && tokens.get(i + 2).getKey().equals("ACb") && tokens.get(i + 2).getVal().equals("[")) {
                            int close = ExpressionUtils.getClosingAccess(i + 1, tokens);
                            List<Token> tokenList = ExpressionUtils.getRange(i + 2, close - 1, tokens);
                            ExpressionUtils.removeFromTo(i + 1, close, tokens);
                            Token tok = ExpressionUtils.evaluateTokens(tokenList, variableState, executor, expressionState);

                            Object o = null;
                            if (ExpressionUtils.isType(tok, "int")) {
                                o = v.getValue((int)tok.getVal());
                            }
                            if (ExpressionUtils.isType(tok, "lon")) {
                                o = v.getValue((int)(long)tok.getVal());
                            }
                            if (ExpressionUtils.isType(tok, "num")) {
                                o = v.getValue((int)(double)tok.getVal());
                            }

                            if (o == null) {
                                continue;
                            }
                            tokens.set(i, new Token(v.getType(), o));
                        } else {
                            tokens.set(i, new Token("arr", array));
                        }
                    } else {
                        tokens.set(i, new Token(v.getType(), v.getValue()));
                    }
                } else {
                    String pointer = ExpressionUtils.getPointer(tokens, i);
                    expressionState.addError("The Variable '" + token.getVal() + "' is not assigned\n" + pointer);
                }
            }

        }
    }

    private void specialFunctions() {
        StringFunctions.execute(tokens, variableState, executor, expressionState);

        ArrayFunctions.execute(tokens, variableState, executor, expressionState);
    }

    private void buildOther() {
        List<Integer> priorities = ExpressionUtils.getPriorities(tokens, 1);
        int max = ExpressionUtils.getMax(priorities);
        while (max != -1) {
            specialFunctions();
            ExpressionUtils.removeBrackets(tokens);

            if (tokens.get(max).getKey().equals("LOG")) {
                if (tokens.get(max).getKey().equals("LOG") && tokens.get(max).getVal().toString().equals("!!")) {

                    if (!tokens.get(max + 1).getKey().equals("bol")) {
                        continue;
                    }

                    tokens.set(max + 1, new Token("bol", !(boolean)tokens.get(max + 1).getVal()));

                    tokens.remove(max);
                    priorities.remove(max);

                    max = ExpressionUtils.getMax(priorities);
                    continue;
                }

                boolean b = ExpressionUtils.compare(tokens.get(max - 1), tokens.get(max), tokens.get(max + 1));

                priorities.remove(max + 1);
                tokens.remove(max + 1);

                priorities.set(max, 0);
                tokens.set(max, new Token("bol", b));

                priorities.remove(max - 1);
                tokens.remove(max - 1);
            } else {
                List<List<Token>> logic = ExpressionUtils.getLogic(tokens, max);
                List<Token> tokenList = ExpressionUtils.evaluateTokenList(logic, variableState, executor, expressionState);

                boolean b = ExpressionUtils.compare(tokenList.get(0), tokens.get(max), tokenList.get(1));
                ExpressionUtils.removeLogic(tokens, new Token("bol", b), priorities, max);
            }
            max = ExpressionUtils.getMax(priorities);
        }

        ExpressionUtils.removeBrackets(tokens);
        specialFunctions();

        if (tokens.size() == 1) {
            expressionState.setResult(tokens.get(0));
            return;
        }

        int startIndex = 0;
        while (startIndex != -1) {
            specialFunctions();
            int start = ExpressionUtils.getOpenBracket(startIndex, tokens);
            if (start == -1) {
                startIndex = start;
                continue;
            }
            int stop = ExpressionUtils.getClosingBracket(start, tokens);
            List<Token> range = ExpressionUtils.getRange(start, stop, tokens);

            if (ExpressionUtils.isCalculation(range)) {
                Calculator calculator = new Calculator(range);
                Object output = calculator.calculate();

                Token t = ExpressionUtils.getToken(output);

                ExpressionUtils.removeFromTo(start, stop, tokens);
                tokens.add(start, t);
            } else {
                startIndex = start;
                if (startIndex != -1) {
                    startIndex++;
                }
            }
        }

        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getKey().equals("STb")) {
                tokens.remove(i);
            }
        }

        specialFunctions();

        for (int i = 0; i < tokens.size() - 2; i += 3) {
            if (!(!tokens.get(i).getKey().equals("OPE") && tokens.get(i + 1).getKey().equals("OPE") && tokens.get(i + 1).getVal().equals("+") && !tokens.get(i + 2).getKey().equals("OPE"))) {
                String pointer = ExpressionUtils.getPointer(tokens, i + 1, i + 2);
                expressionState.addError("Too many or too few Operators\n" + pointer);
            }
        }

        StringBuilder st = new StringBuilder();
        for (Token token : tokens) {
            if (token.getKey().equals("OPE") && token.getVal().equals("+")) {
                continue;
            }
            st.append(token.getVal().toString());
        }

        expressionState.setResult(new Token("str", st.toString()));

    }

}

class StringFunctions {

    private StringFunctions() {
        throw new IllegalStateException("Utility class");
    }

    public static void execute(List<Token> tokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        for (int i = 0; i < tokens.size(); i++) {
            if (i > tokens.size() - 3) {
                continue;
            }

            if (!tokens.get(i).getKey().equals("str")) {
                continue;
            }

            if (!(tokens.get(i + 1).getKey().equals("SEP") && tokens.get(i + 1).getVal().equals("."))) {
                continue;
            }

            if (!tokens.get(i + 2).getKey().equals("COD")) {
                continue;
            }

            if (!(tokens.get(i + 3).getKey().equals("STb") && tokens.get(i + 3).getVal().equals("("))) {
                continue;
            }

            int c = ExpressionUtils.getClosingBracket(i + 3, tokens);
            List<Token> bracketTokens = ExpressionUtils.getRange(i + 3, c, tokens);

            String name = tokens.get(i + 2).getVal().toString();

            Token t = null;

            switch (name) {
                case "length":
                    t = lengthFunction(tokens.get(i));
                    break;
                case "trim":
                    t = trimFunction(tokens.get(i));
                    break;
                case "toUpperCase":
                    t = toUpperCaseFunction(tokens.get(i));
                    break;
                case "toLowerCase":
                    t = toLowerCaseFunction(tokens.get(i));
                    break;
                case "substring":
                    t = substringFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "isEmpty":
                    t = isEmptyFunction(tokens.get(i));
                    break;
                case "isBlank":
                    t = isBlankFunction(tokens.get(i));
                    break;
                case "charAt":
                    t = charAtFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "startsWith":
                    t = startsWithFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "endsWith":
                    t = endsWithFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "indexOf":
                    t = indexOfFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "lastIndexOf":
                    t = lastIndexOfFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "repeat":
                    t = repeatFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "contains":
                    t = containsFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "containsIgnoreCase":
                    t = containsIgnoreCaseFuncion(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "equals":
                    t = equalsFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                case "equalsIgnoreCase":
                    t = equalsIgnoreCaseFunction(tokens.get(i), bracketTokens, variableState, executor, expressionState);
                    break;
                default:
                    break;
            }

            ExpressionUtils.removeFromTo(i, c, tokens);
            tokens.add(i, t);
        }
    }

    private static Token lengthFunction(Token token) {
        int i = token.getVal().toString().length();
        return new Token("int", i);
    }

    private static Token trimFunction(Token token) {
        String s = token.getVal().toString().trim();
        return new Token("str", s);
    }

    private static Token toUpperCaseFunction(Token token) {
        String s = token.getVal().toString().toUpperCase();
        return new Token("str", s);
    }

    private static Token toLowerCaseFunction(Token token) {
        String s = token.getVal().toString().toLowerCase();
        return new Token("str", s);
    }

    private static Token substringFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "int");
        if (!b) {
            return token;
        }

        if (list.isEmpty()) {
            return token;
        }

        int i = (int)list.get(0).getVal();
        if (i < 0) {
            i = 0;
        }
        if (i > token.getVal().toString().length()) {
            i = token.getVal().toString().length();
        }
        if (list.size() == 2) {
            int j = (int)list.get(1).getVal();
            if (j > token.getVal().toString().length()) {
                j = token.getVal().toString().length();
            }

            if (j < i) {
                j = i;
            }

            list.set(1, new Token("int", j));
        }
        list.set(0, new Token("int", i));

        if (list.size() == 1) {
            String s = token.getVal().toString().substring((int)list.get(0).getVal());
            return new Token("str", s);
        }
        if (list.size() == 2) {
            String s = token.getVal().toString().substring((int)list.get(0).getVal(), (int)list.get(1).getVal());
            return new Token("str", s);
        }

        return token;
    }

    private static Token isEmptyFunction(Token token) {
        boolean b = token.getVal().toString().isEmpty();
        return new Token("bol", b);
    }

    private static Token isBlankFunction(Token token) {
        boolean b = token.getVal().toString().isBlank();
        return new Token("bol", b);
    }

    private static Token charAtFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "int");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            int i = (int)list.get(0).getVal();
            return new Token("chr", token.getVal().toString().charAt(i));
        }

        return token;
    }

    private static Token startsWithFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            String s = list.get(0).getVal().toString();
            return new Token("bol", token.getVal().toString().startsWith(s));
        }

        return token;
    }

    private static Token endsWithFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            String s = list.get(0).getVal().toString();
            return new Token("bol", token.getVal().toString().endsWith(s));
        }

        return token;
    }

    private static Token indexOfFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str", "int");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            if (!list.get(0).getKey().equals("str")) {
                return token;
            }
            String s = list.get(0).getVal().toString();
            return new Token("int", token.getVal().toString().indexOf(s));
        }
        if (list.size() == 2) {
            if (!list.get(0).getKey().equals("str") || !list.get(1).getKey().equals("int")) {
                return token;
            }

            String s = list.get(0).getVal().toString();
            int i = (int)list.get(0).getVal();
            return new Token("int", token.getVal().toString().indexOf(s, i));
        }

        return token;
    }

    private static Token lastIndexOfFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str", "int");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            if (!list.get(0).getKey().equals("str")) {
                return token;
            }
            String s = list.get(0).getVal().toString();
            return new Token("int", token.getVal().toString().lastIndexOf(s));
        }
        if (list.size() == 2) {
            if (!list.get(0).getKey().equals("str") || !list.get(1).getKey().equals("int")) {
                return token;
            }

            String s = list.get(0).getVal().toString();
            int i = (int)list.get(1).getVal();
            return new Token("int", token.getVal().toString().lastIndexOf(s, i));
        }

        return token;
    }

    private static Token repeatFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "int");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            int i = (int)list.get(0).getVal();
            try {
                return new Token("str", token.getVal().toString().repeat(i));
            } catch (OutOfMemoryError | IllegalArgumentException e) {
                return token;
            }
        }

        return token;
    }

    private static Token containsFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            String s = list.get(0).getVal().toString();
            return new Token("bol", token.getVal().toString().contains(s));
        }

        return token;
    }

    private static Token containsIgnoreCaseFuncion(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            String s = list.get(0).getVal().toString();
            return new Token("bol", token.getVal().toString().toLowerCase().contains(s.toLowerCase()));
        }

        return token;
    }

    private static Token equalsFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            String s = list.get(0).getVal().toString();
            return new Token("bol", token.getVal().toString().equals(s));
        }

        return token;
    }

    private static Token equalsIgnoreCaseFunction(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<List<Token>> tokenList = ExpressionUtils.getSplitTokens(bracketTokens);
        List<Token> list = ExpressionUtils.evaluateTokenList(tokenList, variableState, executor, expressionState);

        boolean b = ExpressionUtils.isType(list, "str");
        if (!b) {
            return token;
        }

        if (list.size() == 1) {
            String s = list.get(0).getVal().toString();
            return new Token("bol", token.getVal().toString().equalsIgnoreCase(s));
        }

        return token;
    }

}

class ArrayFunctions {

    private ArrayFunctions() {
        throw new IllegalStateException("Utility class");
    }

    public static void execute(List<Token> tokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        for (int i = 0; i < tokens.size(); i++) {
            if (i > tokens.size() - 3) {
                continue;
            }

            if (!tokens.get(i).getKey().equals("arr")) {
                continue;
            }

            if (!(tokens.get(i + 1).getKey().equals("SEP") && tokens.get(i + 1).getVal().equals("."))) {
                continue;
            }

            if (!tokens.get(i + 2).getKey().equals("COD")) {
                continue;
            }

            if (!(tokens.get(i + 3).getKey().equals("STb") && tokens.get(i + 3).getVal().equals("("))) {
                continue;
            }

            int c = ExpressionUtils.getClosingBracket(i + 3, tokens);
            List<Token> bracketTokens = ExpressionUtils.getRange(i + 3, c, tokens);

            String name = tokens.get(i + 2).getVal().toString();

            Token t = null;

            switch (name) {
                case "length":
                case "size":
                    t = lengthFunction(tokens.get(i));
                    break;
                case "isEmpty":
                    t = isEmptyFunction(tokens.get(i));
                    break;
                default:
                    break;
            }

            ExpressionUtils.removeFromTo(i, c, tokens);
            tokens.add(i, t);
        }
    }

    private static Token lengthFunction(Token token) {
        int i = ((ArrayList)token.getVal()).size();
        return new Token("int", i);
    }

    private static Token isEmptyFunction(Token token) {
        boolean b = ((ArrayList)token.getVal()).isEmpty();
        return new Token("bol", b);
    }

}