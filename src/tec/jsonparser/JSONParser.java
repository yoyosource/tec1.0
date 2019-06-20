package tec.jsonparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JSONParser {
	String url = "tec.json";
	public JSONParser(String file) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		url = bufferedReader.readLine();
	}
}
