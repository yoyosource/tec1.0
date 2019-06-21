/*
 * Copyright (c) YoyoNow and Technotype, 2019
 *
 * GNU License
 *
 */

package tec;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import tec.codeexecutor.Executor;
import tec.codeexecutor.Implementor;
import tec.codeexecutor.Lexer;
import tec.codeexecutor.statements.PrintStatement;
import tec.codeexecutor.statements.VarStatement;
import tec.codescanner.CommentScanner;
import tec.codescanner.FileScanner;
import tec.codescanner.TrimmerManager;
import tec.net.Internet;
import tec.utils.FileUtils;
import tec.utils.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Main Class
 * since it's a pure Java project
 * ---
 */
@SuppressWarnings("InstantiatingObjectToGetClassObject")
public class Tec {

    /**
     * Instantiates a new Tec.
     */
    public Tec() {}

    private static Tec tec = new Tec();

    /**
     * The constant debug.
     */
    public static boolean debug = false;
    /**
     * The constant expressions.
     */
    public static int expressions = 1;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception, in case an error happens
     */
    public static void main(String[] args) throws Exception {

        InputStream is = tec.getClass().getResourceAsStream("tec.json");
        JsonObject json = (JsonObject) Jsoner.deserialize(new FileReader(FileUtils.inputStreamToFile(is)));
        for (String arg : args) {
            if (arg.equals("--info")) {
                PrintStream out = System.out;
                out.println("tec executor v" + json.get("version") + " by " + json.get("authors"));
                out.println("Updates:");
                for (Object obj : (JsonArray) json.get("updates")) {
                    out.println();
                }
                return;
            }
            if (arg.equals("--updates")) {
                Internet.readURL("");
            }
            if (arg.equals("-debug")) {
                System.out.println("Running debug mode : showing extra information.");
                debug = true;
            }
        }

        debug = true;

        String url;
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        System.out.println("Tec file Path:");
        url = bufferedReader.readLine();

        long time = System.currentTimeMillis();

        FileScanner fileScanner = new FileScanner();
        fileScanner.scan(new File(url));

        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText());

        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText());

        Lexer lexer = new Lexer();
        lexer.createTokens(commentScanner.getCode());

        long time2 = System.currentTimeMillis();

        ArrayList<Token> tokens = lexer.getTokens();

        String s = tokens.stream().map(token -> token.getKey().equals("NNN") ? "\n" : "<" + token.getKey() + ">" + token.getVal() + " ").collect(Collectors.joining(""));

        System.out.println(s);

        System.out.println("---");
        System.out.println("Tokens       > " + tokens.size());
        System.out.println("Compile Time > " + (time2 - time));
        System.out.println("---");

        Implementor implementor = new Implementor();
        implementor.add(new PrintStatement());
        implementor.add(new VarStatement());

        Executor executor = new Executor(tokens, implementor);
        executor.run();
    }

}
