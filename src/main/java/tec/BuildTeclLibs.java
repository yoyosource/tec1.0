package tec;

import tec.codeexecutor.Lexer;
import tec.codescanner.CommentScanner;
import tec.codescanner.FileScanner;
import tec.codescanner.TrimmerManager;
import tec.utils.Token;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BuildTeclLibs {

    private static ArrayList<String> imports = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }

        System.out.println(args[0]);

        try {
            generateTecL(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to create tecl file");
        }
    }

    private static void generateTecL(String arg) throws FileNotFoundException {
        FileScanner fileScanner = new FileScanner();
        fileScanner.scan(new FileInputStream(arg), "");

        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText(), fileScanner.isTecc());

        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText(), fileScanner.isTecc());

        String code = commentScanner.getCode();

        List<String> strings = addNewImports(code);

        Lexer lexer = new Lexer();
        lexer.createTokens(strings.stream().collect(Collectors.joining("\n")), fileScanner.isTecc());

        List<Token> tokens = lexer.getTokens();

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
                tecl.append(token.getVal());
            }
            try {
                File filel = new File(new File(arg).getPath() + "l");
                filel.delete();
                PrintWriter writer = new PrintWriter(filel);
                writer.print(tecl);
                writer.close();
                filel.setReadOnly();
            } catch (FileNotFoundException e) {

            }
        }
    }

    private static List<String> addNewImports(String code) {
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
                imports.add(st);
                strings.remove(i);
                i--;
            } else if (strings.get(i).trim().length() == 0) {

            } else {
                break;
            }
        }
        return strings;
    }

}
