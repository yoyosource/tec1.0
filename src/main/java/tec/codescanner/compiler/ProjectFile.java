package tec.codescanner.compiler;

import tec.codeexecutor.Lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectFile {

    private String path;
    private byte[] content;

    private Compression compression;

    private static List<String> stringSuffix = new ArrayList<>();

    static {
        stringSuffix.add(".tec");
        stringSuffix.add(".txt");
        stringSuffix.add(".cnf");
        stringSuffix.add(".java");
        stringSuffix.add(".rtf");
        stringSuffix.add(".yml");
        stringSuffix.add(".tecProject");
        stringSuffix.add(".md");
        stringSuffix.add(".xml");
        stringSuffix.add(".csv");
        stringSuffix.add(".tecc");
        stringSuffix.add(".tecl");
    }

    public ProjectFile(File file, int substring) {
        path = file.getAbsolutePath().substring(substring);

        byte[] bytes = new byte[0];
        try {
            InputStream inputStream = new FileInputStream(file);
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {

        }
        content = bytes;

        if (getFileSuffix().equals(".tec")) {

            Lexer lexer = new Lexer();
            lexer.createTokens(getContentAsString(), false);

            TeccParser teccParser = new TeccParser();
            content = teccParser.createTecc(lexer.getTokens());
        }
        if (stringSuffix.contains(getFileSuffix())) {
            compression = new Compression(this);
        }
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        StringBuilder st = new StringBuilder();
        for (byte b : content) {
            st.append((char)b);
        }
        return st.toString();
    }

    public String getPath() {
        return path;
    }

    public String getFileSuffix() {
        return path.substring(path.lastIndexOf('.'));
    }

    public String getFileName() {
        return path.substring(path.lastIndexOf('/'));
    }

    public String getFileNameWithoutSuffix() {
        return path.substring(path.lastIndexOf('/'), path.lastIndexOf('.') - 1);
    }

    @Override
    public String toString() {
        if (stringSuffix.contains(getFileSuffix())) {
            return "ProjectFile{" +
                    "path='" + path + '\'' +
                    ", content=" + getContentAsString() +
                    '}';
        }

        return "ProjectFile{" +
                "path='" + path + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
