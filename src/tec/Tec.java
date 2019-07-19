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

        long time31 = System.currentTimeMillis();

        FileScanner fileScanner = new FileScanner();
        fileScanner.scan(new File(url));

        long time32 = System.currentTimeMillis();

        long time21 = System.currentTimeMillis();

        TrimmerManager trimmerManager = new TrimmerManager();
        trimmerManager.trim(fileScanner.getText(), fileScanner.isTecc());

        CommentScanner commentScanner = new CommentScanner();
        commentScanner.removeComments(trimmerManager.getText(), fileScanner.isTecc());

        long time22 = System.currentTimeMillis();

        long time11 = System.currentTimeMillis();

        Lexer lexer = new Lexer();
        lexer.createTokens(commentScanner.getCode(), fileScanner.isTecc());

        long time12 = System.currentTimeMillis();

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
        System.out.println(stringBuilder);

        if (!fileScanner.isTecc()) {
            StringBuilder tecl = new StringBuilder();
            tecl.append("%checksum ");
            tecl.append(fileScanner.getCheckSum());
            boolean b = false;
            for (Token token : tokens) {
                tecl.append("\n");
                tecl.append("<");
                tecl.append(token.getKey());
                tecl.append(">");
                tecl.append(token.getVal());
            }
            try {
                File file = new File(fileScanner.getPath() + "l");
                file.delete();
                PrintWriter writer = new PrintWriter(file);
                writer.print(tecl);
                writer.close();
                file.setReadOnly();
            } catch (FileNotFoundException e) {

            }
        }

        System.out.println("---");

        System.out.println("Tokens       > " + tokens.size());
        System.out.println("");

        long total = time12 - time11 + time22 - time21 + time32 - time31;
        long other = time32 - time31 - fileScanner.getTime() - fileScanner.getTimeC() - fileScanner.getCheckSumTime();
        String[] strings = time(new long[]{time32 - time31, fileScanner.getTime(), fileScanner.getCheckSumTime(), fileScanner.getTimeC(), other, time22 - time21, time12 - time11, total});
        System.out.println("Scan Time    > " + strings[0]);
        System.out.println("  File Scan  > " + strings[1]);
        System.out.println("  Checksum   > " + strings[2]);
        System.out.println("  Bin Scan   > " + strings[3]);
        System.out.println("  other      > " + strings[4]);
        System.out.println("Process Time > " + strings[5]);
        System.out.println("Lex Time     > " + strings[6]);
        System.out.println("Total Time   > " + strings[7]);

        System.out.println("---");

        Implementor implementor = new Implementor();
        implementor.add(new PrintStatement());

        implementor.add(new VarStatement());
        implementor.add(new ConstStatement());
        implementor.add(new CastStatement());

        implementor.add(new SleepStatement());

        implementor.add(new IfStatement());
        implementor.add(new ElseStatement());

        implementor.add(new WhileStatement());

        implementor.add(new FuncStatement());
        implementor.add(new ReturnStatement());

        Executor executor = new Executor(tokens, implementor);
        executor.run();
    }

    public static String[] time(long[] longs) {
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
