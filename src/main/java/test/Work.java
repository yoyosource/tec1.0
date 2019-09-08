package test;

import java.io.File;
import java.io.FileOutputStream;

public class Work {

    public static void main(String[] args) throws Exception {
        byte[] data = new byte[]{0x0E, 0x01, 0x0F, 0x00, 0x00, 0x00, 0x00, 0x54, 0x49, 0x4D, 0x45, 0x53, 0x54, 0x41, 0x4D, 0x50, 0x0C, (byte)0x80, 0x01, 0x00, 0x00};
        FileOutputStream fos = new FileOutputStream(new File("/Users/jojo/IdeaProjects/tec/src/main/resources/test.tecc"));
        fos.write(data);
        fos.flush();
        fos.close();
    }

}
