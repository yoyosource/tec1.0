package tec.codescanner;

import tec.Tec;
import tec.utils.FileUtils;
import tec.utils.Token;

import javax.swing.text.html.parser.Entity;
import java.io.*;
import java.util.*;

public class TeccParser {

    private List<Replace> keyWordList = new ArrayList<>();

    public TeccParser() {
        keyWordList.add(new Replace(0x80, "print"));
        keyWordList.add(new Replace(0x81, ""));
        keyWordList.add(new Replace(0x82, "var"));
        keyWordList.add(new Replace(0x83, "let"));
        keyWordList.add(new Replace(0x84, ""));
        keyWordList.add(new Replace(0x85, "while"));
        keyWordList.add(new Replace(0x86, "for"));
        keyWordList.add(new Replace(0x87, "sleep"));
        keyWordList.add(new Replace(0x88, ""));
        keyWordList.add(new Replace(0x89, "func"));
        keyWordList.add(new Replace(0x8A, "return"));
        keyWordList.add(new Replace(0x8B, ""));
        keyWordList.add(new Replace(0x8C, "if"));
        keyWordList.add(new Replace(0x8D, "else"));
        keyWordList.add(new Replace(0x8E, ""));
        keyWordList.add(new Replace(0x8F, "cast"));

        keyWordList.add(new Replace(0x90, "true"));
        keyWordList.add(new Replace(0x91, "false"));
        keyWordList.add(new Replace(0x92, ""));
        keyWordList.add(new Replace(0x93, "root"));
        keyWordList.add(new Replace(0x94, "sin"));
        keyWordList.add(new Replace(0x95, "cos"));
        keyWordList.add(new Replace(0x96, "tan"));
        keyWordList.add(new Replace(0x97, "asin"));
        keyWordList.add(new Replace(0x98, "acos"));
        keyWordList.add(new Replace(0x99, "atan"));
        keyWordList.add(new Replace(0x9A, "sigmoid"));
        keyWordList.add(new Replace(0x9B, "gauss"));
        keyWordList.add(new Replace(0x9C, "sig"));
        keyWordList.add(new Replace(0x9D, "ln"));
        keyWordList.add(new Replace(0x9E, "log"));
        keyWordList.add(new Replace(0x9F, ""));

        keyWordList.add(new Replace(0xA0, "=="));
        keyWordList.add(new Replace(0xA1, "<="));
        keyWordList.add(new Replace(0xA2, ">="));
        keyWordList.add(new Replace(0xA3, "!="));
        keyWordList.add(new Replace(0xA4, "equals"));
        keyWordList.add(new Replace(0xA5, "equalsIgnoreCase"));
        keyWordList.add(new Replace(0xA6, "contains"));
        keyWordList.add(new Replace(0xA7, "containsIgnoreCase"));
        keyWordList.add(new Replace(0xA8, "typeof"));
        keyWordList.add(new Replace(0xA9, "canbe"));
        keyWordList.add(new Replace(0xAA, ""));
        keyWordList.add(new Replace(0xAB, "->"));
        keyWordList.add(new Replace(0xAC, ""));
        keyWordList.add(new Replace(0xAD, "&&"));
        keyWordList.add(new Replace(0xAE, "||"));
        keyWordList.add(new Replace(0xAF, "!!"));

        keyWordList.add(new Replace(0xB0, "!&"));
        keyWordList.add(new Replace(0xB1, "x|"));
        keyWordList.add(new Replace(0xB2, "n|"));
        keyWordList.add(new Replace(0xB3, "xn"));
        keyWordList.add(new Replace(0xB4, ""));
        keyWordList.add(new Replace(0xB5, "length"));
        keyWordList.add(new Replace(0xB6, "trim"));
        keyWordList.add(new Replace(0xB7, "toUpperCase"));
        keyWordList.add(new Replace(0xB8, "toLowerCase"));
        keyWordList.add(new Replace(0xB9, "substring"));
        keyWordList.add(new Replace(0xBA, "isEmpty"));
        keyWordList.add(new Replace(0xBB, "isBlank"));
        keyWordList.add(new Replace(0xBC, "charAt"));
        keyWordList.add(new Replace(0xBD, "startsWith"));
        keyWordList.add(new Replace(0xBE, "endsWith"));
        keyWordList.add(new Replace(0xBF, "indexOf"));

        keyWordList.add(new Replace(0xC0, "lastIndexOf"));
        keyWordList.add(new Replace(0xC1, "repeat"));
        keyWordList.add(new Replace(0xC2, "replace"));
        keyWordList.add(new Replace(0xC3, "replaceAll"));
        keyWordList.add(new Replace(0xC4, ""));
        keyWordList.add(new Replace(0xC5, "size"));
        keyWordList.add(new Replace(0xC6, ""));
        keyWordList.add(new Replace(0xC7, ""));
        keyWordList.add(new Replace(0xC8, ""));
        keyWordList.add(new Replace(0xC9, ""));
        keyWordList.add(new Replace(0xCA, ""));
        keyWordList.add(new Replace(0xCB, ""));
        keyWordList.add(new Replace(0xCC, ""));
        keyWordList.add(new Replace(0xCD, ""));
        keyWordList.add(new Replace(0xCE, ""));
        keyWordList.add(new Replace(0xCF, ""));

        keyWordList.add(new Replace(0xD0, "base"));
        keyWordList.add(new Replace(0xD1, "hash"));
        keyWordList.add(new Replace(0xD2, "file"));
        keyWordList.add(new Replace(0xD3, "scanner"));
        keyWordList.add(new Replace(0xD4, "writer"));
        keyWordList.add(new Replace(0xD5, ""));
        keyWordList.add(new Replace(0xD6, ""));
        keyWordList.add(new Replace(0xD7, ""));
        keyWordList.add(new Replace(0xD8, ""));
        keyWordList.add(new Replace(0xD9, ""));
        keyWordList.add(new Replace(0xDA, ""));
        keyWordList.add(new Replace(0xDB, ""));
        keyWordList.add(new Replace(0xDC, ""));
        keyWordList.add(new Replace(0xDD, ""));
        keyWordList.add(new Replace(0xDE, ""));
        keyWordList.add(new Replace(0xDF, ""));
    }

    public byte[] createTecc(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            String s = token.getVal().toString();
            if (token.getKey().equals("str")) {
                s = "\"" + s + "\"";
            }
            if (token.getKey().equals("chr")) {
                s = "'" + s + "'";
            }
            if (token.getKey().equals("typ")) {
                s = "*" + s;
            }
            if (token.getKey().endsWith("lon")) {
                s = s + "L";
            }

            tokens.set(i, new Token(token.getKey(), s));
        }

        List<Map.Entry<String, Integer>> counts = count(tokens);
        Collections.sort(counts, Comparator.comparing(Map.Entry<String, Integer>::getValue));

        List<Replace> replaces = new ArrayList<>();
        int i = 0;
        while (counts.size() > 0 && replaces.size() < 65535) {
            replaces.add(new Replace(i, counts.get(counts.size() - 1).getKey()));
            i++;
            counts.remove(counts.size() - 1);
        }

        List<Byte> bytes = new ArrayList<>();
        addVersion(bytes);
        addReplaces(bytes, replaces);
        bytes.add((byte)0x0C);
        addCode(bytes, tokens, replaces);
        cleanUp(bytes);

        return toArray(bytes);
    }

    private byte[] toArray(List<Byte> bytes) {
        byte[] b = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            b[i] = bytes.get(i);
        }
        return b;
    }

    private List<Map.Entry<String, Integer>> count(List<Token> tokens) {
        Map<String, Integer> counts = new HashMap<>();

        for (Token token : tokens) {
            if (token.getVal().toString().length() < 4) {
                continue;
            }
            if (replaceKeyWord(token.getVal().toString()) != 0x00) {
                continue;
            }

            String s = token.getVal().toString();
            if (!counts.containsKey(s)) {
                counts.put(s, 0);
            }
            counts.put(s, counts.get(s) + 1);
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            int length = entry.getKey().length();
            int time = entry.getValue() * length;
            int replaceWith = 3 * entry.getValue();
            int reduce = time - replaceWith;
            int added = 3 + length;
            if (reduce > added) {
                entry.setValue(reduce - added);
                entryList.add(entry);
            }
        }

        return entryList;
    }

    private void addVersion(List<Byte> bytes) {
        String[] s = new Scanner(Tec.class.getResourceAsStream("/tec/info/tec.cnf")).useDelimiter("\\A").next().split("\n")[1].split(" ")[2].split("\\.");
        int i1 = Integer.parseInt(s[0]);
        int i2 = Integer.parseInt(s[1]);

        bytes.add((byte)0x0E);
        bytes.add((byte)i1);
        bytes.add((byte)0x0F);
        bytes.add((byte)i2);
    }

    private void addReplaces(List<Byte> bytes, List<Replace> replaces) {
        for (Replace replace : replaces) {
            int number = replace.getReplaceNumber();
            int remain = number % 256;
            number = (number - remain) / 256;
            bytes.add((byte)0x00);
            bytes.add((byte)number);
            bytes.add((byte)remain);
            byte[] replaceBytes = replace.getReplaceString().getBytes();
            for (byte b : replaceBytes) {
                bytes.add(b);
            }
        }
    }

    private void addCode(List<Byte> bytes, List<Token> tokens, List<Replace> replaces) {
        for (Token token : tokens) {
            if (replaceKeyWord(token.getVal().toString()) != 0x00) {
                bytes.add(replaceKeyWord(token.getVal().toString()));
                continue;
            }
            if (replace(token.getVal().toString(), replaces).length != 0) {
                bytes.add((byte)0x01);
                bytes.add(replace(token.getVal().toString(), replaces)[0]);
                bytes.add(replace(token.getVal().toString(), replaces)[1]);
                if (token.getKey().equals("typ")) {
                    bytes.add((byte)0x20);
                }
                continue;
            }
            if (token.getKey().equals("###")) {
                continue;
            }
            if (addLineBreak(bytes, token)) {
                continue;
            }
            addSpace(bytes, token);
            byte[] replaceBytes = token.getVal().toString().getBytes();
            for (byte b : replaceBytes) {
                if (b < 0) {
                    bytes.add((byte)0x07);
                }
                bytes.add(b);
            }
            if (token.getKey().equals("typ")) {
                bytes.add((byte)0x20);
            }
        }
    }

    private void cleanUp(List<Byte> bytes) {
        List<Byte> nBytes = new ArrayList<>();
        for (int i = 0; i < bytes.size(); i++) {
            if (bytes.get(i) == (byte)0x0A) {
                if (i > 0 && (bytes.get(i - 1) == (byte)0x7B || bytes.get(i - 1) == (byte)0x7D)) {
                    continue;
                }
                if (i < bytes.size() - 1 && (bytes.get(i + 1) == (byte)0x7B || bytes.get(i + 1) == (byte)0x7D)) {
                    continue;
                }
                if (i < bytes.size() - 1) {
                    int value = (int)bytes.get(i + 1);
                    value = value * -1 + 128;
                    if ((value >= 0x80 && value <= 0x8F) || value >= 0xD0) {
                        continue;
                    }
                }
            }
            nBytes.add(bytes.get(i));
        }

        bytes.clear();
        bytes.addAll(nBytes);
    }

    private void addSpace(List<Byte> bytes, Token token) {
        if (token.getKey().equals("OPE")) {
            return;
        }
        if (token.getKey().equals("BLb")) {
            return;
        }
        if (token.getKey().equals("STb")) {
            return;
        }
        if (token.getKey().equals("ACb")) {
            return;
        }
        if (token.getKey().equals("COD")) {
            return;
        }
        if (token.getKey().equals("ASG")) {
            return;
        }
        if (token.getKey().equals("SEP")) {
            return;
        }
        if (token.getKey().equals("str")) {
            return;
        }
        if (bytes.get(bytes.size() - 1) == 0x0A) {
            return;
        }
        if (bytes.get(bytes.size() - 1) < 0x00) {
            return;
        }
        bytes.add((byte)0x20);
    }

    private boolean addLineBreak(List<Byte> bytes, Token token) {
        if (!token.getKey().equals("NNN")) {
            return false;
        }
        bytes.add((byte)0x0A);
        return true;
    }

    private byte[] replace(String string, List<Replace> replaces) {
        for (Replace replace : replaces) {
            if (replace.getReplaceString().trim().isEmpty()) {
                continue;
            }
            if (replace.getReplaceString().equals(string)) {
                int number = replace.getReplaceNumber();
                int remain = number % 256;
                number = (number - remain) / 256;
                return new byte[]{(byte)number, (byte)remain};
            }
        }
        return new byte[0];
    }

    private byte replaceKeyWord(String string) {
        for (Replace replace : keyWordList) {
            if (replace.getReplaceString().trim().isEmpty()) {
                continue;
            }
            if (replace.getReplaceString().equals(string)) {
                return (byte)replace.getReplaceNumber();
            }
        }
        return 0x00;
    }

    public String parseTecc(InputStream inputStream) {
        byte[] bytes;
        try {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            return "";
        }

        if (!checkTecc(bytes)) {
            return "";
        }
        if (!checkVersion(bytes)) {
            return "";
        }

        return replaceBytes(bytes, getCodeStart(bytes), getReplaces(bytes));
    }

    private boolean checkTecc(byte[] bytes) {
        if (bytes.length <= 5) {
            return false;
        }

        if (bytes[0] != 0x0E) {
            return false;
        }

        if (bytes[2] != 0x0F) {
            return false;
        }

        return true;
    }

    private boolean checkVersion(byte[] bytes) {
        try {
            int v1 = (int) bytes[1];

            String s = new Scanner(Tec.class.getResourceAsStream("/tec/info/tec.cnf")).useDelimiter("\\A").next().split("\n")[1].split(" ")[2].split("\\.")[0];
            int v2 = Integer.parseInt(s);

            if (v1 > v2) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<Replace> getReplaces(byte[] bytes) {
        int i = 4;
        int startReplace = 0;

        List<Replace> replaces = new ArrayList<>();
        Replace replace = null;

        while (!(startReplace == 0 && bytes[i] == 0x0C)) {
            if (startReplace == 0 && bytes[i] == 0x00) {
                startReplace = 2;

                if (replace != null) {
                    replaces.add(replace);
                    replace = null;
                }
            } else if (startReplace > 0) {
                startReplace--;
            } else if (startReplace == 0 && replace == null) {
                int number = ((int) bytes[i - 2] * 256) + (int) bytes[i - 1];

                replace = new Replace();
                replace.setReplaceNumber(number);
                replace.setReplaceString("" + (char) bytes[i]);
            } else if (replace != null) {
                replace.setReplaceString(replace.getReplaceString() + (char) bytes[i]);
            }
            i++;
        }

        if (replace != null) {
            replaces.add(replace);
        }

        return replaces;
    }

    private int getCodeStart(byte[] bytes) {
        int i = 4;
        int startReplace = 0;

        while (!(startReplace == 0 && bytes[i] == 0x0C)) {
            if (startReplace == 0 && bytes[i] == 0x00) {
                startReplace = 2;
            } else if (startReplace > 0) {
                startReplace--;
            }
            i++;
        }
        return i;
    }

    private String replaceBytes(byte[] bytes, int j, List<Replace> replaces) {
        StringBuilder st = new StringBuilder();

        boolean specialChar = false;
        for (int i = j; i < bytes.length; i ++) {
            if (specialChar) {
                st.append((char)bytes[i]);
                specialChar = false;
                continue;
            }
            if (bytes[i] == 0x07) {
                specialChar = true;
                continue;
            }
            if (bytes[i] == 0x01) {
                int number = ((int) bytes[i + 1] * 256) + (int) bytes[i + 2];
                st.append(getReplace(number, replaces));
                continue;
            }
            if (bytes[i] == 0x09) {
                st.append("\t");
                continue;
            }
            if (bytes[i] == 0x0A) {
                st.append("\n");
                continue;
            }
            if (bytes[i] >= 0 && bytes[i] < 0x20) {
                continue;
            }
            if (bytes[i] >= 0x20) {
                st.append((char)bytes[i]);
                continue;
            }
            st.append(getReplacedKeyWord(bytes[i]) + " ");
        }

        return st.toString().replaceAll("\n+", "\n");
    }

    private String getReplace(int number, List<Replace> replaces) {
        for (Replace replace : replaces) {
            if (replace.getReplaceString().trim().isEmpty()) {
                continue;
            }
            if (replace.getReplaceNumber() == number) {
                return replace.getReplaceString();
            }
        }
        return "";
    }

    private String getReplacedKeyWord(byte b) {
        for (Replace replace : keyWordList) {
            if (replace.getReplaceString().trim().isEmpty()) {
                continue;
            }
            if ((byte)replace.getReplaceNumber() == b) {
                if ((replace.getReplaceNumber() >= 0x80 && replace.getReplaceNumber() <= 0x8F) || replace.getReplaceNumber() >= 0xD0) {
                    return "\n" + replace.getReplaceString();
                }
                return replace.getReplaceString();
            }
        }
        return "";
    }

}

class Replace {

    public Replace() {

    }

    public Replace(int replaceNumber, String replaceString) {
        this.replaceNumber = replaceNumber;
        this.replaceString = replaceString;
    }

    private int replaceNumber = 0;
    private String replaceString = "";

    public int getReplaceNumber() {
        return replaceNumber;
    }

    public void setReplaceNumber(int replaceNumber) {
        this.replaceNumber = replaceNumber;
    }

    public String getReplaceString() {
        return replaceString;
    }

    public void setReplaceString(String replaceString) {
        this.replaceString = replaceString;
    }

    @Override
    public String toString() {
        return "Replace{" +
                "replaceNumber=" + replaceNumber +
                ", replaceString='" + replaceString + '\'' +
                '}';
    }
}