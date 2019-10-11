package test;

import tec.codescanner.compiler.TeccCompiler;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Work {

    public static void main(String[] args) throws Exception {
        /*
        BigInteger bigInteger = new BigInteger(new byte[]{16});
        System.out.println(bigInteger.toString(16));
        bigInteger = bigInteger.pow(256);
        System.out.println(bigInteger.toString());
        System.out.println(bigInteger.toString(2));
        System.out.println(bigInteger.toString(3));
        System.out.println(bigInteger.toString(4));
        System.out.println(bigInteger.toString(5));
        System.out.println(bigInteger.toString(6));
        System.out.println(bigInteger.toString(7));
        System.out.println(bigInteger.toString(8));
        System.out.println(bigInteger.toString(9));
        System.out.println(bigInteger.toString(10));
        System.out.println(bigInteger.toString(11));
        System.out.println(bigInteger.toString(12));
        System.out.println(bigInteger.toString(13));
        System.out.println(bigInteger.toString(14));
        System.out.println(bigInteger.toString(15));
        System.out.println(bigInteger.toString(16));
        */

        File file = new File("/Users/jojo/TecProjects/test");
        TeccCompiler teccCompiler = new TeccCompiler(file, "src/test.tec");

        File output = new File("/Users/jojo/TecProjects/test/out/output.txt");
        if (output.exists()) {
            output.delete();
        }

        String s = teccCompiler.toString();
        byte[] bytes = new byte[s.length()];
        char[] chars = s.toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        output.createNewFile();
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

}
