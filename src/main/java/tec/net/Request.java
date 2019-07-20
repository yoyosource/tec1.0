package tec.net;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import tec.jsonparser.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
class Response {
	ContentType type;
	String text;
	Response(String text) {
		type = ContentType.UNDEFINED;
		this.text = text;
	}
	Response(String text, ContentType type) {
		this.type = type;
		this.text = text;
	}
	public JsonObject getJSON() throws Exception {
		if (type != ContentType.JSON) throw new Exception("Response type is not JSON");
		return (JsonObject) Jsoner.deserialize(text);
	}
	enum ContentType {
		JSON, HTML, RAW, UNDEFINED;
	}
}
public class Request {
	public static Response send(URL url) throws IOException {
		InputStream in = url.openStream();
		String responseText = "";
		try {
			responseText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Response(responseText);
	}
	public static Response send(URL url, Response.ContentType type) throws IOException {
		InputStream in = url.openStream();
		String responseText = "";
		try {
			responseText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Response(responseText, type);
	}

}
