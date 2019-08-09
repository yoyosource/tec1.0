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
import tec.codeexecutor.statements.*;
import tec.codeexecutor.statements.io.ScannerStatement;
import tec.codeexecutor.statements.io.WriterStatement;
import tec.codeexecutor.statements.special.BaseStatement;
import tec.codeexecutor.statements.special.CastStatement;
import tec.codeexecutor.statements.io.FileStatement;
import tec.codeexecutor.statements.special.HashStatement;
import tec.codescanner.ImportManager;
import tec.utils.DebugLevel;
import tec.utils.FileUtils;
import tec.utils.Token;

import java.io.*;
import java.util.ArrayList;

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

        /*
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
        */

        String url = "";

        boolean skip = false;

        if (args.length == 1) {
            String s = args[0];
            if (new File(s).exists()) {
                skip = true;
                url = args[0];
            }
        }

        if (args.length == 2) {
            if (new File(args[1]).exists()) {
                skip = true;
                url = args[0] + args[1];
            }
        }

        debug = DebugLevel.NONE;

        if (skip) {

        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println("Tec file Path:");
            url = bufferedReader.readLine();
        }

        if (url.startsWith("-advanced ")) {
            debug = DebugLevel.ADVANCED;
            url = url.substring("-advanced ".length());
        }
        if (url.startsWith("-normal ")) {
            debug = DebugLevel.NORMAL;
            url = url.substring("-normal ".length());
        }

        ImportManager importManager = new ImportManager(new File(url));
        String groundPath = importManager.getGroundPath();

        ArrayList<Token> tokens = importManager.getTokens();

        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Token token : tokens) {
            if (token.getKey().equals("NNN")) {
                stringBuilder.append("<" + i + ": NNN>" + "NNN \n");
            } else {
                stringBuilder.append("<" + i + ": " + token.getKey() + ">" + token.getVal().toString().replaceAll("\n", "\\\\n") + " ");
            }
            i++;
        }
        System.out.println(stringBuilder);
        System.out.println("---");


        Implementor implementor = new Implementor();
        implementor.add(new PrintStatement());

        implementor.add(new VarStatement());
        implementor.add(new ConstStatement());
        implementor.add(new CastStatement());

        implementor.add(new FileStatement());
        implementor.add(new ScannerStatement());
        implementor.add(new WriterStatement());

        implementor.add(new SleepStatement());

        implementor.add(new IfStatement());
        implementor.add(new ElseStatement());

        implementor.add(new WhileStatement());

        implementor.add(new FuncStatement());
        implementor.add(new ReturnStatement());

        implementor.add(new HashStatement());
        implementor.add(new BaseStatement());

        Executor executor = new Executor(tokens, implementor);
        executor.setGroundPath(groundPath);
        executor.run();
    }

}
