package tec.codescanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private long time = 0;
    private long timeC = 0;

    private String checkSum = "";
    private long checkSumTime = 0;

    public String getPath() {
        return groundPath + fileName;
    }

    public void scan(String file) {
        scan(new File(file));
    }

    /**
	 * Scan.
	 *
	 * @param file the file
	 */
	public void scan(File file) {
        filePath(file);

        if (fileName.endsWith(".tecl")) {
            long t = System.currentTimeMillis();
            scanFileC(file);
            tecc = true;
            timeC = System.currentTimeMillis() - t;
            return;
        }

        long ch = System.currentTimeMillis();

        String checkSum = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
            byte[] buffer = new byte[4096];
            while (digestInputStream.read(buffer) > -1) {
            }
            MessageDigest digest = digestInputStream.getMessageDigest();
            digestInputStream.close();
            byte[] md5 = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02X", b));
            }
            checkSum = sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        String checkSum2 = checkSumC(new File(file.getAbsolutePath() + "l"));
        this.checkSumTime = System.currentTimeMillis() - ch;

        this.checkSum = checkSum;

        if (checkSum.equals(checkSum2)) {
            long t = System.currentTimeMillis();
            scanFileC(new File(file.getAbsolutePath() + "l"));
            tecc = true;
            timeC = System.currentTimeMillis() - t;
        } else {
            long t = System.currentTimeMillis();
            scanFile(new File(file.getAbsolutePath()
            ));
            time = System.currentTimeMillis() - t;
        }
    }

    private String checkSumC(File file) {
        String out = "";
        try {
            try {
                out = new Scanner(file).useDelimiter("\\A").next();
            } catch (FileNotFoundException e) {

            }
        } catch (NoSuchElementException e) {

        }
        if (out.startsWith("%checksum ")) {
            String s = out.split("\n<")[0];
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

    public long getCheckSumTime() {
        return checkSumTime;
    }

    public long getTime() {
        return time;
    }

    public long getTimeC() {
        return timeC;
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

    private void scanFileC(File file) {
        String out = "";
        try {
            try {
                out = new Scanner(file).useDelimiter("\\A").next();
            } catch (FileNotFoundException e) {

            }
        } catch (NoSuchElementException e) {

        }
        if (out.startsWith("%checksum ")) {
            out = out.substring(("%checksum " + checkSum + " ").length());
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
