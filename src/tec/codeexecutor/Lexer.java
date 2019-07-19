package tec.codeexecutor;

import tec.exceptions.StringException;
import tec.utils.Token;

import java.util.ArrayList;

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
    public void createTokens(String codes, boolean tecc) {
        if (tecc) {
            codes = codes.substring(1);
            String[] strings = codes.split("\n<");
            Token token;
            String s = "";
            String t = "";
            for (String string : strings) {
                s = string.substring(4);
                t = string.substring(0, 3);

                if (t.equals("num")) {
                    token = new Token(t, Float.parseFloat(s));
                } else if (t.equals("int")) {
                    token = new Token(t, Integer.parseInt(s));
                } else if (t.equals("chr")) {
                    token = new Token(t, s.toCharArray()[0]);
                } else if (t.equals("str")) {
                    token = new Token(t, s + "");
                } else {
                    token = new Token(t, s);
                }
                tokens.add(token);
            }
            return;
        }

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
                    switch (c) {
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '[':
                        case ']':
                        case ':':
                            cTokens.addAll(tokify(s.toString()));
                            s = new StringBuilder();
                            cTokens.addAll(tokify(c + ""));
                            break;
                        default:
                            s.append(c);
                            break;
                    }
                } else {
                    s.append(c);
                }
            }

        }

        if (s.length() != 0) {
            cTokens.addAll(tokify(s.toString()));
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
            tokens.add(new Token("num", Float.parseFloat(s)));
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

        if (s.equals("*char") || s.equals("*character") || s.equals("*chr")) {
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
        if (s.equals("*string") || s.equals("*str")) {
            tokens.add(new Token("typ", "str"));
            return tokens;
        }
        if (s.equals("*any")) {
            tokens.add(new Token("typ", "any"));
            return tokens;
        }

        String[] splitter = new String[]{"+", "-", "*", "/", "%", "#", "^", "!", "(", ")", "root", "sin", "cos", "tan", "asin", "acos", "atan", "sigmoid", "sig", "gauss", "ln", "log", ",", ":"};

        int checks = 0;
        for (String check : splitter) {
            if (s.contains(check)) {
                checks++;
            }
        }

        if (checks > 0) {
            String[] strings = splitString(s, splitter, true, false);
            for (String s1 : strings) {
                tokens.addAll(tokify(s1));
            }
        } else {
            tokens.add(new Token("COD", s));
        }
        return tokens;
    }

    private static String[] splitString(String string, String[] splitStrings, boolean reviveSplitted, boolean addToLast) {
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
                if (reviveSplitted) {
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
