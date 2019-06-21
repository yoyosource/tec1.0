package tec.net;


import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Internet {
	private static String token = "b78958089ea555081b967bd94400c55be64e43c7";
	public static Object readURL(String baseurl) throws IOException, JsonException {
		URL url = new URL(baseurl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		InputStream is = connection.getInputStream();

		File temp = File.createTempFile("tec", "tectemp");
		FileOutputStream out = new FileOutputStream(temp);
		int read;
		byte[] bytes = new byte[1024];
		while ((read = is.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}

		temp.deleteOnExit();
		if (baseurl.endsWith(".json")) {
			return (JsonObject) Jsoner.deserialize(new FileReader(temp));
		}
		return null;
	}
}
