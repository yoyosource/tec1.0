package tec.codeexecutor;

import tec.exceptions.DefinitonException;
import tec.exceptions.StringException;
import tec.utils.Token;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The type Lexer.
 */
public class Lexer {
    
    private ArrayList<Token> tokens = new ArrayList<>();
    private StringBuilder s = new StringBuilder();

    /**
     * Create tokens.
     *
     * @param codes the codes
     */
    public void createTokens(String codes) {

        char[] chars = codes.toCharArray();
        ArrayList<Token> cTokens = new ArrayList<>();

        boolean inString = false;
        boolean backspace = false;
        boolean endLine = false;

        for (char c : chars) {

            if (endLine) {
                cTokens.addAll(tokify(s.toString().trim()));
                cTokens.add(new Token("NNN", ""));
                endLine = false;
                s = new StringBuilder();
            }
            if (c == '\n') {
                endLine = true;
            }

            if (c == ' ' && !inString && !backspace) {
                cTokens.addAll(tokify(s.toString()));
                s = new StringBuilder();
            } else if (c == '"') {
                if (!(backspace)) {
                    if (inString) {
                        inString = false;
                    } else {
                        inString = true;
                    }
                } else {
                    backspace = false;
                }
                s.append(c);
            } else if (c == '\\' && inString) {
                backspace = true;
            }
            else {
                if (!inString && !backspace) {
                    if (c == '(' || c == ')') {
                        cTokens.addAll(tokify(s.toString()));
                        s = new StringBuilder();
                        cTokens.addAll(tokify(c + ""));
                    } else if (c == '{' || c == '}') {
                        cTokens.addAll(tokify(s.toString()));
                        s = new StringBuilder();
                        cTokens.addAll(tokify(c + ""));
                    } else if (c == '[' || c == ']') {
                        cTokens.addAll(tokify(s.toString()));
                        s = new StringBuilder();
                        cTokens.addAll(tokify(c + ""));
                    } else if (c == '.') {
                        cTokens.addAll(tokify(s.toString()));
                        s = new StringBuilder();
                        cTokens.addAll(tokify(c + ""));
                    } else if (c == ':') {
                        cTokens.addAll(tokify(s.toString()));
                        s = new StringBuilder();
                        cTokens.addAll(tokify(c + ""));
                    } else {
                        s.append(c);
                    }
                } else {
                    s.append(c);
                }
            }

        }

        if (s.length() != 0) {
            cTokens.addAll(tokify(s.toString()));
        }

        for (int i = cTokens.size() - 1; i >= 0; i--) {
            if (cTokens.get(i).getKey().equals("nul")) {
                cTokens.remove(i);
            }
        }

        tokens = cTokens;

    }

    /**
     * Gets tokens.
     *
     * @return the tokens
     */
    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private ArrayList<Token> tokify(String s) {

        ArrayList<Token> tokens = new ArrayList<>();

        if (s.length() == 0) {
            tokens.add(new Token("nul", "null"));
            return tokens;
        }

        if (s.startsWith("'") && s.endsWith("'")) {
            if (s.substring(1, s.length() - 1).length() == 1) {
                tokens.add(new Token("chr", s.substring(1, s.length() - 1)));
                return tokens;
            }
            tokens.add(new Token("str", s.substring(1, s.length() - 1)));
            return tokens;
        }
        if (s.startsWith("\"") && s.endsWith("\"")) {
            tokens.add(new Token("str", s.substring(1, s.length() - 1)));
            return tokens;
        }
        if (s.matches("-?(\\d+)(\\.(\\d+))")) {
            tokens.add(new Token("num", Double.parseDouble(s)));
            return tokens;
        }
        if (s.matches("-?(\\d+)")) {
            tokens.add(new Token("int", Integer.parseInt(s)));
            return tokens;
        }
        if (s.matches("##[0-9a-f]+")) {
            tokens.add(new Token("int", Integer.parseInt(s, 16)));
            return tokens;
        }
        if (s.matches("(true)|(false)")) {
            if (s.equals("true")) {
                tokens.add(new Token("bol", true));
                return tokens;
            } else {
                tokens.add(new Token("bol", false));
                return tokens;
            }
        }

        if (s.matches("[+\\-*/%#^]|(root)|(sin|cos|tan|asin|acos|atan)|(simoid|gauss|sig)|(ln|log)")) {
            tokens.add(new Token("OPE", s));
            return tokens;
        }
        if (s.matches("(==)|((<|>)[=]?)|(!=)")) {
            tokens.add(new Token("COM", s));
            return tokens;
        }
        if (s.matches("(=)")) {
            tokens.add(new Token("ASG", s));
            return tokens;
        }
        if (s.matches("->")) {
            tokens.add(new Token("RET", s));
            return tokens;
        }
        if (s.matches("(&&)|(\\|\\|)|(!!)|(!&)|(x\\|)|(n\\|)|(xn)")) {
            tokens.add(new Token("LOG", s));
            return tokens;
        }

        if (s.matches("(>>)|(<<)|(»)|(«)")) {
            if (s.matches("(»)")) {
                s = ">>";
            }
            if (s.matches("(«)")) {
                s = "<<";
            }
            tokens.add(new Token("LOb", s));
            return tokens;
        }
        if (s.matches("\\(|\\)")) {
            tokens.add(new Token("STb", s));
            return tokens;
        }
        if (s.matches("\\[|\\]")) {
            tokens.add(new Token("ACb", s));
            return tokens;
        }
        if (s.matches("[\\{\\}]")) {
            tokens.add(new Token("BLb", s));
            return tokens;
        }
        if (s.equals(".") || s.equals(":") || s.equals(",")) {
            tokens.add(new Token("SEP", s));
            return tokens;
        }

        if (s.equals("*char") || s.equals("*character")) {
            tokens.add(new Token("typ", "chr"));
            return tokens;
        }
        if (s.equals("*boolean") || s.equals("*bool") || s.equals("*bol")) {
            tokens.add(new Token("typ", "bol"));
            return tokens;
        }
        if (s.equals("*number") || s.equals("*num")) {
            tokens.add(new Token("typ", "num"));
            return tokens;
        }
        if (s.equals("*integer") || s.equals("*int")) {
            tokens.add(new Token("typ", "int"));
            return tokens;
        }
        if (s.equals("*string")) {
            tokens.add(new Token("typ", "str"));
            return tokens;
        }

        if (s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/") || s.contains("%") || s.contains("#") || s.contains("^") || s.contains("!")) {
            String[] splitter = new String[]{"+", "-", "*", "/", "%", "#", "^", "!", "(", ")", "root", "sin", "cos", "tan", "asin", "acos", "atan", "sigmoid", "sig", "gauss", "ln", "log"};
            String[] strings = splitString(s, splitter, true, false);

            for (String s1 : strings) {
                tokens.addAll(tokify(s1));
            }
        } else {
            tokens.add(new Token("COD", s));
        }
        return tokens;

    }

    private String[] splitString(String string, char[] splitChars, boolean ReviveSplitted, boolean addToLast) {
        char[] chars = string.toCharArray();
        if (chars.length == 0) {
            throw new StringException("No String");
        }
        if (splitChars.length == 0) {
            throw new StringException("No Split Chars");
        }
        ArrayList<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        int lastSplit = 0;
        while (i < chars.length) {
            char c = chars[i];
            int splitCharTest = 0;
            for (char splitChar : splitChars) {
                if (c == splitChar) {
                    splitCharTest++;
                }
            }
            if (splitCharTest == 0) {
                stringBuilder.append(c);
            } else {
                if (ReviveSplitted) {
                    if (addToLast) {
                        stringBuilder.append(c);
                        words.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    } else {
                        words.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(c);
                        words.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                    }
                } else {
                    words.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
                lastSplit = i;
            }
            i++;
        }
        if (lastSplit != string.length()) {
            words.add(stringBuilder.toString());
        }
        return words.toArray(new String[0]);
    }

    private static String[] splitString(String string, String[] splitStrings, boolean ReviveSplitted, boolean addToLast) {
        char[] chars = string.toCharArray();
        if (chars.length == 0) {
            throw new StringException("No String");
        }
        if (splitStrings.length == 0) {
            throw new StringException("No Split Strings");
        }

        ArrayList<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        int lastSplit = 0;

        while (i < chars.length) {
            int splitStringTest = 0;
            char c = chars[i];
            String s = "";
            for (String st : splitStrings) {
                StringBuilder sb = new StringBuilder();
                int index = i;
                int currentIndex = i;
                while (currentIndex < chars.length && currentIndex < index + st.length()) {
                    sb.append(chars[currentIndex]);
                    currentIndex++;
                }
                if (sb.toString().equals(st)) {
                    if (s.length() == 0) {
                        s = st;
                    }
                    splitStringTest++;
                }
            }

            if (splitStringTest == 0) {
                stringBuilder.append(c);
            } else {
                i += s.length() - 1;
                if (ReviveSplitted) {
                    if (addToLast) {
                        words.add(stringBuilder.toString() + s);
                    } else {
                        words.add(stringBuilder.toString());
                        words.add(s);
                    }
                } else {
                    words.add(stringBuilder.toString());
                }
                stringBuilder = new StringBuilder();
                lastSplit = i;
            }
            i++;
        }
        if (lastSplit != string.length()) {
            words.add(stringBuilder.toString());
        }
        return words.toArray(new String[0]);
    }

}
