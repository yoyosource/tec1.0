package tec.github;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHub {
	private String token = "b78958089ea555081b967bd94400c55be64e43c7";
	public static Object readURL(String baseurl) throws IOException {
		URL url = new URL(baseurl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		if (baseurl.endsWith(".json")) {

		}
		return null;
	}
}
