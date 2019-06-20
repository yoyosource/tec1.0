package tec.utils;

import java.io.*;

public class FileUtils {
	public static File inputStreamToFile(InputStream is) throws IOException {
		OutputStream outputStream = null;
		try
		{
			File file = File.createTempFile("tec", ".tectmp");
			outputStream = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			file.deleteOnExit();
		}
		finally
		{
			if (outputStream != null)
			{
				outputStream.close();
			}
		}
	}
}
