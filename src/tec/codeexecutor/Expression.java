package tec.codeexecutor;

import tec.calculator.Calculator;
import tec.utils.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Expression {

    private Calculator calculator = new Calculator();

    private ArrayList<Token> tokens = new ArrayList<>();

    public Expression() {

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


    String outputString;
    boolean outputBoolean;

    public void build() {
        if (isLogic()) {
            booleanOutput();
        } else {
            stringOutput();
        }
    }

    private void booleanOutput() {

    }

    private void stringOutput() {
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
                throw new IllegalArgumentException("To many or to few Operators");
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
        return outputString;
    }

    public boolean getBoolean() {
        return outputBoolean;
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
