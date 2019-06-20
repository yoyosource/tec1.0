/*
 * Copyright (c) YoyoNow and Technotype, 2019
 *
 * GNU License
 *
 */

package tec;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import tec.codeexecutor.Executor;
import tec.codeexecutor.Implementor;
import tec.codeexecutor.Lexer;
import tec.codeexecutor.statements.Print;
import tec.codescanner.CommentScanner;
import tec.codescanner.FileScanner;
import tec.codescanner.TrimmerManager;
import tec.exceptions.DefinitonException;
import tec.jsonparser.JSONObject;
import tec.jsonparser.JSONParser;
import tec.utils.FileUtils;
import tec.utils.Token;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Main Class
 * since it's a pure Java project
 * ---
 */
public class Tec {
    public Tec() {}
    public static void main(String[] args) throws IOException, JsonException {
        /*JSONParser parser = new JSONParser("tec.json");
        JSONObject tec = parser.read();*/
        InputStream is = new Tec().getClass().getClassLoader().getResourceAsStream("tec.json");
        JsonObject json = (JsonObject) Jsoner.deserialize(new FileReader(FileUtils.inputStreamToFile(is)));
        for (String arg : args) {
            if (arg.equals("--info")) {
                PrintStream out = System.out;
                out.println("tec executor v" + json.get("version"));
                out.println("");
                return;
            }
            if (arg.equals("-updates")) {
                URL updateJson = new URL("");
            }
        }


        String url = "/Users/jojo/IdeaProjects/tec/src/test.tec";
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
        implementor.add(new Print());

        Executor executor = new Executor(tokens, implementor);
        executor.run();
    }

}
