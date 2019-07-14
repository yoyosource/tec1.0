/*
 * Copyright (c) YoyoNow and Technotype, 2019
 *
 * GNU License
 *
 */

package tec;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import tec.codeexecutor.Executor;
import tec.codeexecutor.Implementor;
import tec.codeexecutor.Lexer;
import tec.codeexecutor.statements.*;
import tec.codescanner.CommentScanner;
import tec.codescanner.FileScanner;
import tec.codescanner.TrimmerManager;
import tec.utils.DebugLevel;
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
    public static DebugLevel debug = DebugLevel.NONE;
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
                out.println("tec executor v" + json.get("version") + " by " + json.get("authors") );
                out.println("Updates:");
                for (String s: (String[]) json.get("updates")) {
                    
                }
                return;
            }
            if (arg.equals("-updates")) {
            }
            if (arg.startsWith(":debug.")) {
            	arg = arg.substring(7);
                System.out.println("Running debug mode");
                debug = DebugLevel.getDebugLevel(arg);
            }
        }

        debug = DebugLevel.NORMAL;

        String url;

        if (true) {
            url = "/Users/jojo/IdeaProjects/tec/src/test.tec";
            debug = DebugLevel.NONE;
        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println("Tec file Path:");
            url = bufferedReader.readLine();
        }

        FileScanner fileScanner = new FileScanner();
        fileScanner.scan(new File(url));

        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText());

        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText());

        long time = System.currentTimeMillis();

        Lexer lexer = new Lexer();
        lexer.createTokens(commentScanner.getCode());

        long time2 = System.currentTimeMillis();

        ArrayList<Token> tokens = lexer.getTokens();

        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Token token : tokens) {
            if (token.getKey().equals("NNN")) {
                stringBuilder.append("<" + i + ": NNN>" + "NNN \n");
            } else {
                stringBuilder.append("<" + i + ": " + token.getKey() + ">" + token.getVal() + " ");
            }
            i++;
        }

        StringBuilder stringBuilderc = new StringBuilder();
        for (Token token : tokens) {
            stringBuilderc.append("<" + token.getKey() + ">" + token.getVal() + "\n");
        }

        System.out.println(stringBuilderc + "\n\n" + stringBuilder);

        System.out.println("---");
        System.out.println("Tokens       > " + tokens.size());
        if (time2 - time > 1000) {
            System.out.println("Compile Time > " + (((float)(time2 - time)) / 1000) + "s");
        } else {
            System.out.println("Compile Time > " + (time2 - time) + "ms");
        }
        System.out.println("---");

        Implementor implementor = new Implementor();
        implementor.add(new PrintStatement());

        implementor.add(new VarStatement());
        implementor.add(new ConstStatement());

        implementor.add(new SleepStatement());

        implementor.add(new IfStatement());
        implementor.add(new ElseStatement());

        implementor.add(new WhileStatement());

        implementor.add(new FuncStatement());
        implementor.add(new ReturnStatement());

        Executor executor = new Executor(tokens, implementor);
        executor.run();
    }

}
