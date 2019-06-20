package tec.jsonparser;
import com.github.cliftonlabs.json_simple.*;

import java.io.*;
import java.lang.reflect.Field;

public class JSONParser {
	private File file;
	public JSONParser(String file) {
		this.file = new File(file);
	}
	public JSONObject read() throws JsonException, IOException {
		JsonObject json = (JsonObject) Jsoner.deserialize(new FileReader(file));
		Object[] array = json.values().toArray();
		for (Object o : array) {
			Field[] fields = o.getClass().getFields();

		}
		return null;
	}
}
