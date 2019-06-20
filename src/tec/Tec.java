/*
 * Copyright (c) YoyoNow and Technotype, 2019
 *
 * GNU License
 *
 */

package tec;

import tec.codeexecutor.Lexer;
import tec.codescanner.CommentScanner;
import tec.codescanner.FileScanner;
import tec.codescanner.TrimmerManager;
import tec.utils.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Main Class
 * since it's a pure Java project
 * ---
 */
public class Tec {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Tec file Path:");
        String url = scanner.next();

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

        System.out.println("elirgöirug");

        System.out.println("---");
        System.out.println("Tokens       > " + tokens.size());
        System.out.println("Compile Time > " + (time2 - time));
        System.out.println("---");
    }
}
