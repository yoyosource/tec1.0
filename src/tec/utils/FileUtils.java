package tec.utils;

import java.io.*;

public class FileUtils {
	public static File inputStreamToFile(InputStream is) throws Exception {
		OutputStream outputStream = null;
		try
		{
			File file = File.createTempFile("tec", ".tectmp");
			outputStream = new FileOutputStream(file);

			int read;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			file.deleteOnExit();
			return file;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if (outputStream != null)
			{
				outputStream.close();
			}
		}
		throw new Exception("An error occurred while executing.");
	}
}
