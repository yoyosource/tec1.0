package tec.codeexecutor;

import tec.utils.Token;

import java.util.ArrayList;
import java.util.Arrays;
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

        ExpressionUtils.removeBrackets(tokens);

        replaceFunctions();
        replaceVariables();

        specialFunctions();

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
                    tokens.set(i, new Token(variableState.getVarType(tokens.get(i).getVal().toString()), variableState.getVarValue(tokens.get(i).getVal().toString())));
                } else {
                    String pointer = ExpressionUtils.getPointer(tokens, i);
                    expressionState.addError("The Variable '" + token.getVal() + "' is not assigned\n" + pointer);
                }
            }

        }
    }

    private void specialFunctions() {
        StringFunctions.execute(tokens, variableState, executor, expressionState);
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

class ExpressionUtils {

    private ExpressionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getPointer(List<Token> tokens, int... indices) {
        List<Integer> points = new ArrayList<>();

        int length = 0;
        for (int i = 0; i < tokens.size(); i++) {
            if (i != 0) {
                length++;
            }

            boolean b = false;
            for (int j : indices) {
                if (j  == i) {
                    b = true;
                }
            }
            if (b) {
                points.add(length);
            }

            length += tokens.get(i).getVal().toString().length();
        }

        List<Integer> tokenEnd = new ArrayList<>();

        StringBuilder st1 = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            if (i != 0) {
                tokenEnd.add(st1.length() - 1);
                st1.append(" ");
            }
            st1.append(tokens.get(i).getVal().toString());
        }

        StringBuilder st2 = new StringBuilder();
        boolean inUnderline = false;
        for (int i = 0; i < length; i++) {
            if (inUnderline) {
                st2.append("~");
            } else {
                if (points.contains(i)) {
                    st2.append("^");
                    inUnderline = true;
                } else {
                    st2.append(" ");
                }
            }
            if (tokenEnd.contains(i)) {
                inUnderline = false;
            }
        }

        return st1 + "\n" + st2;
    }

    public static void removeBrackets(List<Token> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getKey().equals("STb")) {
                continue;
            }
            if (i > 0 && i < tokens.size() - 1 && tokens.get(i - 1).getKey().equals("STb") && tokens.get(i - 1).getVal().equals("(") && tokens.get(i + 1).getKey().equals("STb") && tokens.get(i + 1).getVal().equals(")")) {
                tokens.remove(i + 1);
                tokens.remove(i - 1);
            }
        }
    }

    public static int getClosingBracket(int i, List<Token> tokens) {
        if (!(tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("("))) {
            return i;
        }

        int bracket = 1;
        i++;
        for (int j = i; j < tokens.size(); j++) {
            if (tokens.get(j).getKey().equals("STb") && tokens.get(j).getVal().equals("(")) {
                bracket++;
            }
            if (tokens.get(j).getKey().equals("STb") && tokens.get(j).getVal().equals(")")) {
                bracket--;
            }
            if (bracket == 0) {
                i = j;
                break;
            }
        }
        return i;
    }

    public static int getOpenBracket(int start, List<Token> tokens) {
        for (int i = start; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("(")) {
                return i;
            }
        }
        return -1;
    }

    public static List<Token> getRange(int start, int stop, List<Token> tokens) {
        List<Token> newTokens = new ArrayList<>();
        for (int i = start; i <= stop; i++) {
            newTokens.add(tokens.get(i));
        }
        return newTokens;
    }

    public static List<List<Token>> getSplitTokens(List<Token> bracketTokens) {
        if (bracketTokens.get(0).getKey().equals("STb") && bracketTokens.get(0).getVal().equals("(")) {
            bracketTokens.remove(0);
        }
        if (bracketTokens.get(bracketTokens.size() - 1).getKey().equals("STb") && bracketTokens.get(bracketTokens.size() - 1).getVal().equals(")")) {
            bracketTokens.remove(bracketTokens.size() - 1);
        }

        List<List<Token>> tokenList = new ArrayList<>();
        List<Token> list = new ArrayList<>();
        for (int i = 0; i < bracketTokens.size(); i++) {
            if (bracketTokens.get(i).getKey().equals("SEP") && bracketTokens.get(i).getVal().equals(",")) {
                tokenList.add(list);
                list = new ArrayList<>();
            } else {
                list.add(bracketTokens.get(i));
            }
        }

        if (!list.isEmpty()) {
            tokenList.add(list);
        }

        return tokenList;
    }

    public static List<Token> evaluateTokenList(List<List<Token>> tokenList, VariableState variableState, Executor executor, ExpressionState expressionState) {
        List<Token> list = new ArrayList<>();

        for (List<Token> tokens : tokenList) {
            Expression expression = new Expression(tokens, variableState, executor);
            expression.build();

            ExpressionState exState = expression.getResult();
            expressionState.addTime(exState.getTime());
            if (exState.hasErrors()) {
                for (String s : exState.getErrors()) {
                    expressionState.addError(s);
                }
                list.add(null);
            } else {
                list.add(exState.getResult());
            }
        }

        return list;
    }

    public static boolean isCalculation(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i).getKey();
            if (!token.equals("int") && !token.equals("num") && !token.equals("lon") && !token.equals("OPE") && !token.equals("STb")) {
                return false;
            }
        }
        return true;
    }

    public static Token getToken(Object o) {
        if (o instanceof Integer) {
            return new Token("int", (int)o);
        }
        if (o instanceof Double) {
            return new Token("num", (double)o);
        }
        if (o instanceof Long) {
            return new Token("lon", (long)o);
        }

        if (o instanceof Character) {
            return new Token("chr", (char)o);
        }
        return new Token("str", o.toString());
    }

    public static void removeFromTo(int start, int stop, List<Token> tokens) {
        for (int i = stop; i >= start; i--) {
            tokens.remove(i);
        }
    }

    public static boolean isType(Token[] list, String... types) {
        return isType(Arrays.asList(list), types);
    }

    public static boolean isType(List<Token> list, String... types) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                return false;
            }
            boolean b = false;
            for (String s : types) {
                if (list.get(i).getKey().equals(s)) {
                    b = true;
                }
            }
            if (!b) {
                return false;
            }
        }
        return true;
    }

    public static List<Integer> getPriorities(List<Token> list, int mode) {
        List<Integer> priorities = new ArrayList<>();

        int p = 0;
        for (Token t : list) {
            if (t.getKey().equals("STb") && t.getVal().equals("(")) {
                if (mode == 1) {
                    p += 2;
                } else {
                    p++;
                }
            }
            if (mode == 1 && t.getKey().equals("COM")) {
                priorities.add(p + 2);
            } else if (mode == 1 && t.getKey().equals("LOG")) {
                priorities.add(p + 1);
            } else if (mode == 0 && t.getKey().equals("OPE") && t.getVal().equals("+")) {
                priorities.add(p + 1);
            }
            else {
                priorities.add(0);
            }
            if (t.getKey().equals("STb") && t.getVal().equals(")")) {
                if (mode == 1) {
                    p -= 2;
                } else {
                    p--;
                }
            }
        }

        return priorities;
    }

    public static int getMax(List<Integer> list) {
        if (list.isEmpty()) {
            return -1;
        }

        int value = list.get(0);
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (value < list.get(i)) {
                value = list.get(i);
                index = i;
            }
        }

        if (list.get(index) == 0) {
            return -1;
        }

        return index;
    }

    public static List<List<Token>> getLogic(List<Token> tokens, int index) {
        if (!tokens.get(index).getKey().equals("COM")) {
            return new ArrayList<>();
        }

        List<List<Token>> lists = new ArrayList<>();

        List<Token> tokenList = new ArrayList<>();
        for (int i = index - 1; i >= 0; i--) {
            if (tokens.get(i).getKey().equals("STb") || tokens.get(i).getKey().equals("LOG")) {
                break;
            }
            tokenList.add(0, tokens.get(i));
        }
        lists.add(tokenList);
        tokenList = new ArrayList<>();

        for (int i = index + 1; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("STb") || tokens.get(i).getKey().equals("LOG")) {
                break;
            }
            tokenList.add(tokens.get(i));
        }
        lists.add(tokenList);

        return lists;
    }

    public static void removeLogic(List<Token> tokens, Token token, List<Integer> priorities, int index) {
        if (!tokens.get(index).getKey().equals("COM")) {
            return;
        }

        int j = 0;
        for (int i = index + 1; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("STb") || tokens.get(i).getKey().equals("LOG")) {
                break;
            }
            j = i;
        }

        for (int i = j; i >= index + 1; i--) {
            tokens.remove(i);
            priorities.remove(i);
        }

        tokens.set(index, token);
        priorities.set(index, 0);

        for (int i = index - 1; i >= 0; i--) {
            if (tokens.get(i).getKey().equals("STb") || tokens.get(i).getKey().equals("LOG")) {
                break;
            }
            tokens.remove(i);
            priorities.remove(i);
        }
    }

    public static boolean compare(Token token1, Token compare, Token token2) {
        String compareString = compare.getVal().toString();

        if (compare.getKey().equals("COM")) {
            if (token2.getKey().equals("typ")) {
                return compareType(token1, token2, compareString);
            }

            if (compareString.equals("==")) {
                return token1.getVal().toString().equals(token2.getVal().toString());
            }
            if (compareString.equals("!=")) {
                return !token1.getVal().toString().equals(token2.getVal().toString());
            }

            return compareOther(token1, token2, compareString);
        } else if (compare.getKey().equals("LOG")) {
            return compareLogic(token1, token2, compareString);
        } else {
            return false;
        }
    }

    private static boolean compareType(Token token1, Token token2, String compareString) {
        if (compareString.equals("typeof")) {
            return token1.getKey().equals(token2.getVal().toString());
        }

        if (compareString.equals("canbe")) {

            String typeTo = token2.getVal().toString();

            if (typeTo.equals("str")) {
                return true;
            }
            if (typeTo.equals("int")) {
                try {
                    Integer.parseInt(token1.getVal().toString());
                    return true;
                } catch (Exception e) {
                    // Ignoring Exception
                }
                try {
                    if (!token1.getVal().toString().startsWith("##")) {
                        return false;
                    }
                    Integer.parseInt(token1.getVal().toString().substring(2), 16);
                    return true;
                } catch (Exception e) {
                    // Ignoring Exception
                }
                return false;
            }
            if (typeTo.equals("num")) {
                try {
                    Double.parseDouble(token1.getVal().toString());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            if (typeTo.equals("bol")) {
                return true;
            }
            if (typeTo.equals("chr")) {
                return true;
            }

        }

        return false;
    }

    private static boolean compareOther(Token token1, Token token2, String compareString) {
        boolean b = isType(new Token[]{token1, token2}, "int", "num", "lon");
        if (!b) {
            return false;
        }

        if (compareString.equals(">=")) {
            if (token1.getKey().equals("int") && token2.getKey().equals("int")) {
                return (int)token1.getVal() >= (int)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("num")) {
                return (int)token1.getVal() >= (double)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("lon")) {
                return (int)token1.getVal() >= (long)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("int")) {
                return (double)token1.getVal() >= (int)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("num")) {
                return (double)token1.getVal() >= (double)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("lon")) {
                return (double)token1.getVal() >= (long)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("int")) {
                return (long)token1.getVal() >= (int)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("num")) {
                return (long)token1.getVal() >= (double)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("lon")) {
                return (long)token1.getVal() >= (long)token2.getVal();
            }
        }
        if (compareString.equals("<=")) {
            if (token1.getKey().equals("int") && token2.getKey().equals("int")) {
                return (int)token1.getVal() <= (int)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("num")) {
                return (int)token1.getVal() <= (double)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("lon")) {
                return (int)token1.getVal() <= (long)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("int")) {
                return (double)token1.getVal() <= (int)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("num")) {
                return (double)token1.getVal() <= (double)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("lon")) {
                return (double)token1.getVal() <= (long)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("int")) {
                return (long)token1.getVal() <= (int)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("num")) {
                return (long)token1.getVal() <= (double)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("lon")) {
                return (long)token1.getVal() <= (long)token2.getVal();
            }
        }
        if (compareString.equals(">")) {
            if (token1.getKey().equals("int") && token2.getKey().equals("int")) {
                return (int)token1.getVal() > (int)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("num")) {
                return (int)token1.getVal() > (double)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("lon")) {
                return (int)token1.getVal() > (long)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("int")) {
                return (double)token1.getVal() > (int)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("num")) {
                return (double)token1.getVal() > (double)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("lon")) {
                return (double)token1.getVal() > (long)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("int")) {
                return (long)token1.getVal() > (int)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("num")) {
                return (long)token1.getVal() > (double)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("lon")) {
                return (long)token1.getVal() > (long)token2.getVal();
            }
        }
        if (compareString.equals("<")) {
            if (token1.getKey().equals("int") && token2.getKey().equals("int")) {
                return (int)token1.getVal() < (int)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("num")) {
                return (int)token1.getVal() < (double)token2.getVal();
            }
            if (token1.getKey().equals("int") && token2.getKey().equals("lon")) {
                return (int)token1.getVal() < (long)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("int")) {
                return (double)token1.getVal() < (int)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("num")) {
                return (double)token1.getVal() < (double)token2.getVal();
            }
            if (token1.getKey().equals("num") && token2.getKey().equals("lon")) {
                return (double)token1.getVal() < (long)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("int")) {
                return (long)token1.getVal() < (int)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("num")) {
                return (long)token1.getVal() < (double)token2.getVal();
            }
            if (token1.getKey().equals("lon") && token2.getKey().equals("lon")) {
                return (long)token1.getVal() < (long)token2.getVal();
            }
        }

        return false;
    }

    private static boolean compareLogic(Token token1, Token token2, String compareString) {
        boolean b = isType(new Token[]{token1, token2}, "bol");
        if (!b) {
            return false;
        }

        if (compareString.equals("||")) {
            return (boolean)token1.getVal() || (boolean)token2.getVal();
        }
        if (compareString.equals("&&")) {
            return (boolean)token1.getVal() && (boolean)token2.getVal();
        }

        if (compareString.equals("!&")) {
            return !((boolean)token1.getVal() && (boolean)token2.getVal());
        }

        if (compareString.equals("n|")) {
            return !((boolean)token1.getVal() || (boolean)token2.getVal());
        }
        if (compareString.equals("x|")) {
            return (((boolean)token1.getVal() || (boolean)token2.getVal()) && !((boolean)token1.getVal() && (boolean)token2.getVal()));
        }
        if (compareString.equals("xn")) {
            return !(((boolean)token1.getVal() || (boolean)token2.getVal()) && !((boolean)token1.getVal() && (boolean)token2.getVal()));
        }

        return false;
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
                    t = containsIgnoreCaseFunciont(tokens.get(i), bracketTokens, variableState, executor, expressionState);
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

    private static Token containsIgnoreCaseFunciont(Token token, List<Token> bracketTokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
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