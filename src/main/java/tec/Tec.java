package tec;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Implementor;
import tec.codeexecutor.Lexer;
import tec.codeexecutor.statements.*;
import tec.codeexecutor.statements.io.FileStatement;
import tec.codeexecutor.statements.io.ScannerStatement;
import tec.codeexecutor.statements.io.WriterStatement;
import tec.codeexecutor.statements.special.BaseStatement;
import tec.codeexecutor.statements.special.CastStatement;
import tec.codeexecutor.statements.special.HashStatement;
import tec.codescanner.FileScanner;
import tec.codescanner.ImportManager;
import tec.codescanner.TeccParser;
import tec.utils.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Tec {

    public static void main(String[] args) {

        if (args.length < 1) {
            helpMessage();
            return;
        }

        String command = args[0];

        if (command.endsWith("-info") || command.equals("-i")) {
            if (args.length == 2) {
                showInfo(args);
                return;
            }
            command = "-v";
        }

        if (command.equals("-version") || command.equals("-v")) {
            InputStream in = Tec.class.getResourceAsStream("/tec/info/tec.cnf");
            String version = new Scanner(in).useDelimiter("\\A").next();
            System.out.println(version);
            return;
        }

        if (command.equals("-help") || command.equals("-h") || command.equals("?")) {
            if (args.length == 2) {
                helpMessage(args[1]);
                System.out.println("");
                System.out.println("You can also use '-i', '-v', '-h', '-r', '-c', '-s'");
                return;
            }
            helpMessage(false);
            System.out.println("");
            System.out.println("You can also use '-i', '-v', '-h', '-r', '-c', '-s'");
            return;
        }

        if (command.equals("-run") || command.equals("-r")) {
            run(args);
            return;
        }

        if (command.equals("-compile") || command.equals("-c")) {
            if (args.length != 2 && args.length != 3 && args.length != 4) {
                helpMessage();
                return;
            }
            compile(args);
            return;
        }

        if (command.equals("-show") || command.equals("-s")) {
            if (args.length != 2 && args.length != 3) {
                helpMessage();
                return;
            }
            show(args);
            return;
        }

        helpMessage();

    }

    private static void helpMessage(String string) {
        if (string.endsWith("compile") || string.endsWith("c")) {
            System.out.println("Help:");
            System.out.println(" '-compile <PATH>' to compile a tec File");
            System.out.println(" '-compile <PATH> -run' to compile a tec File and directly run the compiled file");
            System.out.println(" '-compile <PATH> -show' to compile a tec File and directly show the compiled file");
            System.out.println(" '-compile <PATH> -show -run' to compile a tec File and directly show and run the compiled file");
        } else if (string.endsWith("show") || string.endsWith("s")) {
            System.out.println("Help:");
            System.out.println(" '-show <PATH>' to compile a tec File");
            System.out.println(" '-show <PATH> -run' to show a tec File and directly run the compiled file");
            System.out.println(" '-show <PATH> -compile' to show a tec File and directly compile the file");
        } else if (string.endsWith("info") || string.endsWith("i")) {
            System.out.println("Help:");
            System.out.println(" '-info' to check the tec Version you are using");
            System.out.println(" '-info <PATH>' outputs further information on the file specified");
        } else if (string.endsWith("version") || string.endsWith("v")) {
            System.out.println("Help:");
            System.out.println(" '-version' to check the tec Version you are using");
            System.out.println(" '-version <PATH>' to show the Version of the file");
        } else if (string.endsWith("run") || string.endsWith("r")) {
            System.out.println("Help:");
            System.out.println(" '-run <PATH>' to run a tec File");
            System.out.println(" '-run <COMMAND>' to directly run one single Command");
        }
        else {
            helpMessage(false);
        }
    }

    private static void helpMessage(boolean wrongUsage) {
        if (wrongUsage) {
            System.out.println("Wrong usage:");
        } else {
            System.out.println("Help:");
        }
        System.out.println(" '-run <PATH>' to run a tec File");
        System.out.println(" '-run <COMMAND>' to directly run one single Command");
        System.out.println(" '-compile <PATH>' to compile a tec File");
        System.out.println(" '-show <PATH>' shows the content of the file");
        System.out.println(" '-version' to check the tec Version you are using");
        System.out.println(" '-version <PATH>' to show the Version of the file");
        System.out.println(" '-info' to check the tec Version you are using");
        System.out.println(" '-info <PATH>' outputs further information");
        System.out.println();
        System.out.println(" '-help <KEYWORD>' for more help");
    }

    private static void helpMessage() {
        helpMessage(true);
    }

    private static void run(String[] args) {
        ImportManager importManager = new ImportManager(new File(args[1]));
        String groundPath = importManager.getGroundPath();

        List<Token> tokens = importManager.getTokens();

        System.out.println("Information:");

        if (importManager.getTotal() == -1) {
            List<String> strings = new ArrayList<>();
            for (String st : args) {
                strings.add(st.replaceAll(";", "\n").replaceAll("%n", "\n"));
            }
            strings.remove(0);
            String string = strings.stream().collect(Collectors.joining(" "));
            Lexer lexer = new Lexer();
            lexer.createTokens(string, false);
            tokens = lexer.getTokens();
            System.out.println(" Total Time    > " + lexer.getTime() + "ms");
        } else {
            System.out.println(" Total Time    > " + importManager.getTotal() + "ms");
        }

        System.out.println(" Tokens        > " + tokens.size());

        run(tokens, groundPath);
    }

    private static void run(List<Token> tokens, String groundPath) {
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

    private static void compile(String[] args) {
        if (args[1].endsWith(".tecc")) {
            return;
        }

        ImportManager importManager = new ImportManager(new File(args[1]));
        ArrayList<Token> tokens = importManager.getTokens();
        TeccParser teccParser = new TeccParser();
        byte[] bytes = teccParser.createTecc(tokens);

        double previous = importManager.getLength();
        double now = bytes.length;

        String name = new File(args[1]).getName();
        name = importManager.getGroundPath() + name.substring(0, name.lastIndexOf('.')) + ".tecc";

        if (new File(name).exists()) {
            new File(name).delete();
        }

        System.out.println("Information:");
        System.out.println(" Total Time    > " + importManager.getTotal() + "ms");
        System.out.println(" Tokens        > " + tokens.size());

        try {
            if (previous - now < 0) {
                System.out.println(" Useless Compression because the new file would use more space.");
                return;
            }
            new File(name).createNewFile();
            FileOutputStream fos = new FileOutputStream(name);
            fos.write(bytes);
            fos.flush();
            fos.close();
            System.out.println(" before / after: " + (int)previous + " -> " + (int)now);
            System.out.println(" bytes saved   : " + (int)(previous - now));
            System.out.println("                 ~" + ((double)(int)((previous - now) / previous * 10000.0) / 100.0) + "%");
        } catch (IOException e) {
            System.out.println(" The file could not be created.");
        }

        if (args.length == 3) {
            String arg = args[2];
            if (arg.equals("-run") || arg.equals("-r")) {
                args[1] = args[1] + "c";
                run(args);
            }
            if (arg.equals("-show") || arg.equals("-s")) {
                args[1] = args[1] + "c";
                System.out.println();
                show(args);
            }
        }

        if (args.length == 4) {
            String arg1 = args[2];
            String arg2 = args[3];
            if ((arg1.equals("-show") || arg1.equals("-s")) && (arg2.equals("-run") || arg2.equals("-r"))) {
                args[1] = args[1] + "c";
                System.out.println();
                show(args);
                System.out.println();
                try {
                    FileScanner fileScanner = new FileScanner(new FileInputStream(args[1]), args[1]);
                    Lexer lexer = new Lexer();
                    lexer.createTokens(fileScanner.getText(), fileScanner.isTecl());
                    run(lexer.getTokens(), args[0]);
                } catch (IOException e) {
                    return;
                }
            }
        }
    }

    private static void show(String[] args) {
        try {
            FileScanner fileScanner = new FileScanner(new FileInputStream(args[1]), args[1]);
            if (fileScanner.isTecc()) {
                byte[] bytes = new FileInputStream(args[1]).readAllBytes();
                System.out.println("Hex:");
                System.out.println(toHex(bytes));
                System.out.println();
                System.out.println("Decompiled:");
            } else {
                System.out.println("Raw:");
            }
            System.out.println(fileScanner.getText());
            System.out.println();
            System.out.println("Lexed:");
            Lexer lexer = new Lexer();
            lexer.createTokens(fileScanner.getText(), fileScanner.isTecl());
            System.out.println(lexer.getTokens());
        } catch (IOException e) {
            System.out.println(" File not found");
        }

        if (args.length == 3) {
            String arg = args[2];
            if (arg.equals("-run") || arg.equals("-r")) {
                run(args);
            }
            if (arg.equals("-compile") || arg.equals("-c")) {
                System.out.println();
                compile(args);
            }
        }
    }

    private static void showInfo(String[] args) {
        if (!(args[1].endsWith(".tec") || args[1].endsWith(".tecc") || args[1].endsWith(".tecl"))) {
            helpMessage();
            return;
        }
        System.out.println("Information:");
        try {
            byte[] bytes = new FileInputStream(args[1]).readAllBytes();
            if (bytes.length < 5 || args[1].endsWith(".tec") || args[1].endsWith(".tecl")) {
                System.out.println(" Version: unknown");
            } else {
                System.out.println(" Version: " + bytes[1] + "." + bytes[3]);
            }
            System.out.println(" Type: " + args[1].substring(args[1].lastIndexOf('.')));
            System.out.println(" Size: " + bytes.length + " bytes");
            if (args[1].endsWith(".tec")) {
                System.out.println(" Has Tecc: " + new File(args[1] + "c").exists());
            } else {
                System.out.println(" Has Tecc: ?");
            }
        } catch (IOException e) {
            System.out.println(" File not found");
        }
    }

    public static String toHex(byte... a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b: a) {
            sb.append("0x" + String.format("%02x", b).toUpperCase() + " ");
        }
        return sb.toString();
    }

}
