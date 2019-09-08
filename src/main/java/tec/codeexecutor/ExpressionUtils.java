package tec.codeexecutor;

import tec.utils.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExpressionUtils {

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

    public static int getClosingAccess(int i, List<Token> tokens) {
        if (!(tokens.get(i).getKey().equals("ACb") && tokens.get(i).getVal().equals("["))) {
            return i;
        }

        int bracket = 1;
        i++;
        for (int j = i; j < tokens.size(); j++) {
            if (tokens.get(j).getKey().equals("ACb") && tokens.get(j).getVal().equals("[")) {
                bracket++;
            }
            if (tokens.get(j).getKey().equals("ACb") && tokens.get(j).getVal().equals("]")) {
                bracket--;
            }
            if (bracket == 0) {
                i = j;
                break;
            }
        }
        return i;
    }

    public static int getOpenAccess(int start, List<Token> tokens) {
        for (int i = start; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("ACb") && tokens.get(i).getVal().equals("[")) {
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

    public static Token evaluateTokens(List<Token> tokens, VariableState variableState, Executor executor, ExpressionState expressionState) {
        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        ExpressionState exState = expression.getResult();
        expressionState.addTime(exState.getTime());
        if (exState.hasErrors()) {
            for (String s : exState.getErrors()) {
                expressionState.addError(s);
            }
        }
        return exState.getResult();
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
            if (!(token.equals("int") || token.equals("num") || token.equals("lon") || token.equals("OPE") || token.equals("STb"))) {
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

    public static boolean isType(Token token, String... types) {
        return isType(Collections.singletonList(token), types);
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
