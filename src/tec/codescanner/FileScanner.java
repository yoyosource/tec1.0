package tec.codescanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileScanner {

    private String groundPath = "";
    private String fileName = "";

    private String text = "";

    public void scan(File file) {
        filePath(file);
        if (fileName.endsWith(".tec")) {
            scanFile(file);
        }
    }

    public String getText() {
        return text;
    }

    private void scanFile(File file) {
        String out = "";
        try {
            try {
                out = new Scanner(file).useDelimiter("\\A").next();
            } catch (FileNotFoundException e) {

            }
        } catch (NoSuchElementException e) {

        }
        if (!(out.endsWith(" "))) {
            out += " ";
        }
        text = out;
    }

    private void filePath(File file) {
        char[] chars = file.getAbsolutePath().toCharArray();
        int i = chars.length - 1;
        while (chars[i] != '/' && chars[i] != '\\') {
            i--;
        }
        i++;
        groundPath = file.getAbsolutePath().substring(0, i);
        fileName = file.getAbsolutePath().substring(i);
    }

}
