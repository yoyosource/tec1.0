package tec.codescanner;

import tec.Tec;
import tec.codeexecutor.Lexer;
import tec.utils.Token;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImportManager {

    private ArrayList<String> imports = new ArrayList<>();
    private ArrayList<String> doneImports = new ArrayList<>();

    private String groundPath = "";

    private ArrayList<Token> lexTokens = new ArrayList<>();

    private long total = 0;
    private long length = 0;

    public ImportManager(File file) {
        try {
            long time = System.currentTimeMillis();

            importFile(new FileInputStream(file), file.getAbsolutePath());

            groundPath = FileScanner.filePath(file)[0];

            manageAllImports();

            total = System.currentTimeMillis() - time;
        } catch (FileNotFoundException e) {
            total = -1;
        }
    }

    public String getGroundPath() {
        return groundPath;
    }

    public ArrayList<Token> getTokens() {
        return lexTokens;
    }

    public long getLength() {
        return length;
    }

    public long getTotal() {
        return total;
    }

    public void importFile(InputStream inputStream, String tecL) {
        FileScanner fileScanner = new FileScanner(inputStream, tecL);

        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText(), fileScanner.isTecl());

        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText(), fileScanner.isTecl());

        String code = commentScanner.getCode();

        List<String> strings = addNewImports(code);
        length += strings.stream().collect(Collectors.joining("\n")).length();

        Lexer lexer = new Lexer();
        lexer.createTokens(strings.stream().collect(Collectors.joining("\n")), fileScanner.isTecl());

        List<Token> tokens = lexer.getTokens();

        /*
        if (!fileScanner.isTecc()) {
            StringBuilder tecl = new StringBuilder();
            tecl.append("%checksum ");
            tecl.append(fileScanner.getCheckSum());
            for (String cImport : imports) {
                tecl.append("\n");
                tecl.append("import ");
                tecl.append(cImport);
            }
            for (Token token : tokens) {
                tecl.append("\n");
                tecl.append("<");
                tecl.append(token.getKey());
                tecl.append(">");
                tecl.append(token.getVal().toString().replaceAll("\n", "\\\\n"));
            }
            try {
                File filel = new File(tecL);
                filel.delete();
                PrintWriter writer = new PrintWriter(filel);
                writer.print(tecl);
                writer.close();
                filel.setReadOnly();
            } catch (FileNotFoundException e) {

            }
        }
        */

        if (lexTokens.size() == 0) {
            lexTokens.addAll(tokens);
        } else {
            lexTokens.add(0, new Token("NNN", ""));
            lexTokens.addAll(0, tokens);
        }
    }

    private List<String> addNewImports(String code) {
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
        return strings;
    }

    private void manageAllImports() {
        String[] cImports = imports.toArray(new String[0]);
        imports = new ArrayList<>();
        for (String s : cImports) {
            if (doneImports.contains(s))  {
                continue;
            }
            InputStream inputStream;
            String tecl;
            if (s.startsWith("std")) {
                if (s.startsWith("std:")) {
                    s = "std-" + s.substring("std:".length());
                }
                inputStream = Tec.class.getResourceAsStream("/tec/libraries/" + s + ".tec");
                tecl = Tec.class.getResource("/tec/libraries/" + s + ".tecl").getPath();
            } else {
                try {
                    inputStream = new FileInputStream(groundPath + s + ".tec");
                    tecl = groundPath + s + ".tec";
                } catch (FileNotFoundException e) {
                    continue;
                }
            }
            importFile(inputStream, tecl);
            doneImports.add(s);
        }
        if (imports.size() > 0) {
            manageAllImports();
        }
    }

}
