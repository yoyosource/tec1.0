package tec.codescanner;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class FileScanner {

    private String text;

    private boolean tecc = false;
    private boolean tecl = false;

    public FileScanner(InputStream inputStream, String file) {
        String suffix = getSuffix(file);
        if (file.length() > 0 && new File(file).exists()) {
            if (suffix.equals("tec")) {
                text = scanTec(inputStream);
            }
            if (suffix.equals("tecl")) {
                text = scanTecl(inputStream);
                tecl = true;
            }
            if (suffix.equals("tecc")) {
                text = scanTecc(inputStream);
                tecc = true;
            }
        } else {
            if (suffix.equals("tecl")) {
                text = scanTecl(inputStream);
                tecl = true;
            } else {
                text = "";
            }
        }
    }

    private String getSuffix(String file) {
        if (file.length() > 0 && new File(file).exists()) {
            return file.substring(file.lastIndexOf('.') + 1);
        }
        return "";
    }

    private String scanTec(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }

    private String scanTecl(InputStream inputStream) {
        String out = new Scanner(inputStream).useDelimiter("\\A").next();

        if (out.startsWith("%checksum ")) {
            String s = out.split("\n")[0];
            out = out.substring(s.length());
        }

        return out;
    }

    private String scanTecc(InputStream inputStream) {
        TeccParser teccParser = new TeccParser();
        return teccParser.parseTecc(inputStream);
    }

    public String getText() {
        return text;
    }

    public boolean isTecc() {
        return tecc;
    }

    public boolean isTecl() {
        return tecl;
    }

    public static String[] filePath(File file) {
        int i = file.getAbsolutePath().lastIndexOf('/') + 1;

        String fileName = file.getAbsolutePath().substring(i);
        String groundPath = file.getAbsolutePath().substring(0, i);

        return new String[]{groundPath, fileName};
    }

}
