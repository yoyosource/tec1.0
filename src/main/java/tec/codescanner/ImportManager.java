package tec.codescanner;

import tec.Tec;
import tec.codeexecutor.Lexer;
import tec.utils.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ImportManager {

    private ArrayList<String> imports = new ArrayList<>();
    private ArrayList<String> doneImports = new ArrayList<>();

    private String groundPath = "";

    private ArrayList<Token> lexTokens = new ArrayList<>();

    long total = 0;

    public ImportManager(File file) {
        System.out.println("---");
        importFile(file);
        manageAllImports();
        System.out.println("");
        System.out.println("Total Time             > " + total + "ms");
        System.out.println("Tokens                 > " + lexTokens.size());
        System.out.println("---");
    }

    public String getGroundPath() {
        return groundPath;
    }

    public ArrayList<Token> getTokens() {
        return lexTokens;
    }

    public void importFile(InputStream inputStream) {
        FileScanner fileScanner = new FileScanner();
        fileScanner.scan(inputStream);

        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText(), fileScanner.isTecc());

        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText(), fileScanner.isTecc());

        String code = commentScanner.getCode();

        List<String> strings = Arrays.stream(code.split("\n")).collect(Collectors.toList());
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).startsWith("import ")) {
                String st = strings.get(i).substring("import ".length());
                if (st.endsWith(".tecl")) {
                    st = st.substring(0, st.length() - 5);
                }
                if (st.endsWith(".tec")) {
                    st = st.substring(0, st.length() - 4);
                }
                if (!doneImports.contains(st)) {
                    imports.add(st);
                }
                strings.remove(i);
                i--;
            } else if (strings.get(i).trim().length() == 0) {

            } else {
                break;
            }
        }

        Lexer lexer = new Lexer();
        lexer.createTokens(strings.stream().collect(Collectors.joining("\n")), fileScanner.isTecc());

        List<Token> tokens = lexer.getTokens();

        if (lexTokens.size() == 0) {
            lexTokens.addAll(tokens);
        } else {
            lexTokens.addAll(0, tokens);
        }
        lexTokens.add(0, new Token("NNN", ""));
        lexTokens.add(0, new Token("###", ""));
    }

    public void importFile(File file) {
        FileScanner fileScanner = new FileScanner();
        fileScanner.scan(file);
        long checkSumtime = fileScanner.getCheckSumTime();
        long scanTimeFile = fileScanner.getTime();
        long scanTimeFileC = fileScanner.getTimeC();

        long trimmerManagerTime = System.currentTimeMillis();
        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText(), fileScanner.isTecc());
        trimmerManagerTime = System.currentTimeMillis() - trimmerManagerTime;

        long commentScannerTime = System.currentTimeMillis();
        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText(), fileScanner.isTecc());
        commentScannerTime = System.currentTimeMillis() - commentScannerTime;

        String code = commentScanner.getCode();

        List<String> strings = Arrays.stream(code.split("\n")).collect(Collectors.toList());
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).startsWith("import ")) {
                String st = strings.get(i).substring("import ".length());
                if (st.endsWith(".tecl")) {
                    st = st.substring(0, st.length() - 5);
                }
                if (st.endsWith(".tec")) {
                    st = st.substring(0, st.length() - 4);
                }
                if (!doneImports.contains(st)) {
                    imports.add(st);
                }
                strings.remove(i);
                i--;
            } else if (strings.get(i).trim().length() == 0) {

            } else {
                break;
            }
        }

        long lexTime = System.currentTimeMillis();
        Lexer lexer = new Lexer();
        lexer.createTokens(strings.stream().collect(Collectors.joining("\n")), fileScanner.isTecc());
        lexTime = System.currentTimeMillis() - lexTime;

        List<Token> tokens = lexer.getTokens();

        if (groundPath.length() == 0) {
            groundPath = fileScanner.getGroundPath();
            String string = fileScanner.getFileName().replaceAll("\\.tecl", "").replaceAll("\\.tec", "");
            doneImports.add(string);
        }

        if (!fileScanner.isTecc()) {
            StringBuilder tecl = new StringBuilder();
            tecl.append("%checksum ");
            tecl.append(fileScanner.getCheckSum());
            for (String cImport : imports) {
                tecl.append("\n");
                tecl.append("import ");
                tecl.append(cImport);
            }
            boolean b = false;
            for (Token token : tokens) {
                tecl.append("\n");
                tecl.append("<");
                tecl.append(token.getKey());
                tecl.append(">");
                tecl.append(token.getVal());
            }
            try {
                File filel = new File(fileScanner.getPath() + "l");
                filel.delete();
                PrintWriter writer = new PrintWriter(filel);
                writer.print(tecl);
                writer.close();
                filel.setReadOnly();
            } catch (FileNotFoundException e) {

            }
        }

        if (lexTokens.size() == 0) {
            lexTokens.addAll(tokens);
        } else {
            lexTokens.addAll(0, tokens);
        }
        lexTokens.add(0, new Token("NNN", ""));
        lexTokens.add(0, new Token("###", file.getName()));

        String[] times = time(1000000, (scanTimeFile + scanTimeFileC + checkSumtime), scanTimeFile, scanTimeFileC, checkSumtime, trimmerManagerTime, commentScannerTime, lexTime);

        System.out.println("" + "file              > " + file.getName());
        System.out.println("- " + "FileScanner     > " + times[1]);
        System.out.println("  - " + "Scan Time     > " + times[2]);
        System.out.println("  - " + "Bin  Time     > " + times[3]);
        System.out.println("  - " + "CheckSum Time > " + times[4]);
        System.out.println("- " + "TrimmerManager  > " + times[5]);
        System.out.println("- " + "CommenScanner   > " + times[6]);
        System.out.println("- " + "Lex Time        > " + times[7]);

        total += (scanTimeFile + scanTimeFileC + checkSumtime) + trimmerManagerTime + commentScannerTime + lexTime;

    }

    private void manageAllImports() {
        String[] cImports = imports.toArray(new String[0]);
        imports = new ArrayList<>();
        for (String s : cImports) {
            if (doneImports.contains(s))  {
                continue;
            }
            InputStream inputStream;
            if (s.startsWith("std")) {
                inputStream = Tec.class.getResourceAsStream("/tec/libraries/" + s + ".tec");
            } else {
                try {
                    inputStream = new FileInputStream(groundPath + s + ".tec");
                } catch (FileNotFoundException e) {
                    continue;
                }
            }
            importFile(inputStream);
            doneImports.add(s);
        }
        if (imports.size() > 0) {
            manageAllImports();
        }
    }

    public String[] time(long... longs) {
        long longest = 0;
        for (long l : longs) {
            if (l > longest) {
                longest = l;
            }
        }

        int length = (longest + "").length();
        int i = 0;
        int length2 = length;
        while (length2  > 3) {
            i++;
            length2 -= 3;
        }
        length += 0;

        ArrayList<String> strings = new ArrayList<>();

        StringBuilder st = new StringBuilder();
        for (long l : longs) {
            st.append(l);
            while (st.length() < length) {
                st.insert(0, " ");
            }
            st.append("ms");
            strings.add(st.toString());
            st = new StringBuilder();
        }

        return strings.toArray(new String[0]);
    }

}
