package tec.codeexecutor;

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

            if (c == ' ' && inString == false && backspace == false) {
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

        if (s.matches("[+\\-*/%#^]")) {
            tokens.add(new Token("OPE", s));
            return tokens;
        }
        if (s.matches("(==)|(<>[=]?)|(!=)")) {
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
            char[] c = s.toCharArray();
            for (char c1 : c) {
                tokens.addAll(tokify(c1 + ""));
            }
        } else {
            tokens.add(new Token("COD", s));
        }
        return tokens;

    }

}
