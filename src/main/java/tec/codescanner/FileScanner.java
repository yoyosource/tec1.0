package tec.codescanner;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The type File scanner.
 */
public class FileScanner {

    private String groundPath = "";
    private String fileName = "";

    private String text = "";
    private boolean tecc = false;

    private String checkSum = "";

    public String getPath() {
        return groundPath + fileName;
    }

    public String getGroundPath() {
        return groundPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setGroundPath(String groundPath) {
        this.groundPath = groundPath;
    }

    public void scan(InputStream inputStream, String tecl) {

        scan(inputStream);

        if (tecl.length() > 0 && new File(tecl).exists()) {
            try {
                String checkSum = checkSumC(new FileInputStream(tecl));

                if (this.checkSum.equals(checkSum)) {
                    scanFileC(new FileInputStream(tecl));

                    tecc = true;
                }

            } catch (FileNotFoundException e) {

            }
        }

    }

    public void scan(InputStream inputStream) {
        try {
            text = new Scanner(inputStream).useDelimiter("\\A").next();

            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest((text + "").getBytes("UTF-8"));
            String hex = toHex(hash).toLowerCase();

            checkSum = hex;
        } catch (NoSuchElementException | UnsupportedEncodingException | NoSuchAlgorithmException e) {

        }
    }

    private String checkSumC(InputStream file) {
        String out = new Scanner(file).useDelimiter("\\A").next();
        if (out.startsWith("%checksum ")) {
            String s = out.split("\n")[0];
            return s.substring("%chekcsum ".length());
        }
        return "";
    }

	/**
	 * Gets text.
	 *
	 * @return the text
	 */
	public String getText() {
        return text;
    }

    public boolean isTecc() {
        return tecc;
    }

    public String getCheckSum() {
        return checkSum;
    }

    private void scanFileC(InputStream file) {
        String out = new Scanner(file).useDelimiter("\\A").next();
        if (out.startsWith("%checksum ")) {
            out = out.substring(("%checksum " + checkSum + " ").length());
        }
        text = out;
    }

    public void filePath(File file) {
        char[] chars = file.getAbsolutePath().toCharArray();
        int i = chars.length - 1;
        while (chars[i] != '/' && chars[i] != '\\') {
            i--;
        }
        i++;
        groundPath = file.getAbsolutePath().substring(0, i);
        fileName = file.getAbsolutePath().substring(i);
    }

    public String toHex(byte[] bytes) {
        StringBuilder st = new StringBuilder();
        for (byte b : bytes) {
            String s = String.format("%02X", b);
            st.append(s);
        }
        return st.toString();
    }

}
