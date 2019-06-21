package tec.codeexecutor;

import tec.calculator.Calculator;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Expression {

    private Calculator calculator = new Calculator();

    private ArrayList<Token> tokens = new ArrayList<>();
    private VariableState variableState;

    public Expression() {

    }

    public Expression(ArrayList<Token> tokens, VariableState variableState) {
        this.tokens = tokens;
        this.variableState = variableState;
    }

    public Expression(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void add(Token token) {
        tokens.add(token);
    }

    public void add(Token... tokens) {
        this.tokens.addAll(Arrays.stream(tokens).collect(Collectors.toList()));
    }

    public void add(int index, Token token) {
        this.tokens.add(index, token);
    }

    public void remove(int index) {
        tokens.remove(index);
    }


    String Error;
    String outputString;
    boolean outputBoolean;
    long expressionTime;

    public void build() {
        expressionTime = System.currentTimeMillis();
        if (isLogic()) {
            booleanOutput();
        } else {
            stringOutput();
        }
        expressionTime -= System.currentTimeMillis();
        expressionTime *= -1;
    }

    private ArrayList<Token> replaceVars(ArrayList<Token> tokens) {
        if (variableState != null) {
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).getKey().equals("COD")) {
                    if (variableState.isVariable(tokens.get(i).getVal().toString())) {
                        tokens.set(i, new Token(variableState.getVarType(tokens.get(i).getVal().toString()), variableState.getVarValue(tokens.get(i).getVal().toString())));
                    } else {
                        Error = "It is not allowed to have Statements in Expressions or the Variable is not assigned";
                        return null;
                    }
                }
            }
        }
        return tokens;
    }

    private void booleanOutput() {
        ArrayList<Token> compareToken = new ArrayList<>();
        ArrayList<ArrayList<Token>> lineToken = new ArrayList<>();

        ArrayList<Token> nTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("LOG")) {

            }
        }

        outputBoolean = false;
    }

    private void stringOutput() {

        if (replaceVars(tokens) != null) {
            tokens = replaceVars(tokens);
        } else {
            return;
        }

        if (isCalculation(tokens)) {
            float f = calculator.calc(tokens.stream().map(token -> token.getVal().toString()).collect(Collectors.joining()));
            outputString = "" + f;
            return;
        }

        int startIndex = 0;
        int startIndex2 = 0;

        while (startIndex2 != -1) {
            int start = getOpentBracket(startIndex, tokens);
            ArrayList<Token> tokens = getTokensToClosingBracket(this.tokens, start);
            if (isCalculation(tokens)) {
                float f = calculator.calc(tokens.stream().map(token -> token.getVal().toString()).collect(Collectors.joining()));
                tokens = removeToClosingBracket(this.tokens, start);
                tokens.add(start, new Token("num", f));
            } else {
                startIndex = start;
                startIndex2 = start;
                startIndex++;
            }
        }

        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getKey().equals("STb")) {
                tokens.remove(i);
            }
        }

        for (int i = 0; i < tokens.size()-1; i++) {
            if (tokens.get(i).isType() && tokens.get(i + 1).getKey().equals("OPE") && tokens.get(i + 1).getVal().equals("+")) {
                i++;
            } else {
                Error = "To many or to few Operators";
                return;
            }
        }

        outputString = toStringOutput();
    }

    private String toStringOutput() {
        StringBuilder st = new StringBuilder();
        for (Token token : tokens) {
            if (token.getKey().equals("OPE") && token.getVal().equals("+")) {
                continue;
            }
            if (token.getKey().equals("num")) {
                if (token.getVal().toString().endsWith(".0")) {
                    st.append(token.getVal().toString().substring(0, token.getVal().toString().length() - 2));
                } else {
                    st.append(token.getVal().toString());
                }
                continue;
            }
            st.append(token.getVal().toString());
        }
        return st.toString();
    }


    public String getString() {
        if (outputString == null && Error == null) {
            Error = "This Expression was not detected as a String Expression";
        }
        return outputString;
    }

    public boolean getBoolean() {
        if ((Boolean)outputBoolean == null && Error == null) {
            Error = "This Expression was not detected as a Boolean Expression";
        }
        return outputBoolean;
    }

    public String getError() {
        return Error;
    }

    public long getExpressionTime() {
        return expressionTime;
    }


    private int getOpentBracket(int start, ArrayList<Token> tokens) {
        for (int i = start; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("(")) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Token> getTokensToClosingBracket(ArrayList<Token> tokens, int start) {
        if (start == -1) {
            start++;
        }
        ArrayList<Token> arrayList = new ArrayList<>();
        boolean st = false;
        int brCount = 0;
        while (!st || (brCount != 0 && start < tokens.size())) {
            if (tokens.get(start).getKey().equals("STb") && tokens.get(start).getVal().toString().equals("(")) {
                brCount++;
            }
            if (brCount != 0) {
                arrayList.add(tokens.get(start));
            }
            if (tokens.get(start).getKey().equals("STb") && tokens.get(start).getVal().toString().equals(")")) {
                brCount--;
            }
            st = true;
            start++;
        }
        return arrayList;
    }

    private ArrayList<Token> removeToClosingBracket(ArrayList<Token> tokens, int start) {
        int brCount = 0;
        boolean st = false;
        int stop = start;
        while (!st || (brCount != 0 && stop < tokens.size())) {
            if (tokens.get(stop).getKey().equals("STb") && tokens.get(stop).getVal().toString().equals("(")) {
                brCount++;
            }
            if (tokens.get(stop).getKey().equals("STb") && tokens.get(stop).getVal().toString().equals(")")) {
                brCount--;
            }
            st = true;
            stop++;
        }
        stop--;
        while (stop >= start) {
            tokens.remove(stop);
            stop--;
        }
        return tokens;
    }


    private boolean isLogic() {
        for (Token token : tokens) {
            if (token.getKey().equals("LOG")) {
                return true;
            }
            if (token.getKey().equals("COM")) {
                return true;
            }
        }
        return false;
    }

    private boolean isCalculation(ArrayList<Token> tokens) {
        if (tokens.size() == 0) {
            return false;
        }
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i).getKey();
            if (!token.equals("int") && !token.equals("num") && !token.equals("OPE") && !token.equals("STb")) {
                return false;
            }
        }
        return true;
    }

}
