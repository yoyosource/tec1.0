package tec.codescanner.compiler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TeccCompiler {

    private List<ProjectFile> projectFiles = new ArrayList<>();
    private String root = "";

    public TeccCompiler(File file) {
        byte[] bytes = new byte[0];
        try {
            InputStream inputStream = new FileInputStream(file);
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {

        }
    }

    public TeccCompiler(File file, String main) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        if (!file.isDirectory()) {
            throw new FileNotFoundException();
        }
        root = file.getAbsolutePath();

        System.out.println(file.getAbsolutePath());
        long t = System.currentTimeMillis();
        scanProject(file);
        System.out.println("Time: " + (System.currentTimeMillis() - t));
    }

    private void scanProject(File f) {
        File[] files = f.listFiles();
        for (File file : files) {
            if (Files.isSymbolicLink(Paths.get(file.getPath()))) {
                continue;
            }
            if (file.isDirectory()) {
                if (file.getAbsolutePath().contains("/exclude")) {
                    continue;
                }
                if (file.getAbsolutePath().contains("/out")) {
                    continue;
                }
                scanProject(file);
            } else {
                projectFiles.add(new ProjectFile(file, root.length()));
            }
        }
    }

    @Override
    public String toString() {

        StringBuilder st = new StringBuilder();
        for (ProjectFile projectFile : projectFiles) {
            st.append("\n");
            st.append(projectFile.toString());
        }

        return "TeccCompiler{" +
                "projectFiles=" + st +
                ", root='" + root + '\'' +
                '}';
    }
}
