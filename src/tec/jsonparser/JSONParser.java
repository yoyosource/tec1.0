package tec.jsonparser;
import com.github.cliftonlabs.json_simple.*;

import java.io.*;
import java.lang.reflect.Field;

/**
 * The type Json parser.
 */
public class JSONParser {
	private File file;

	/**
	 * Instantiates a new Json parser.
	 *
	 * @param file the file
	 */
	public JSONParser(String file) {
		this.file = new File(file);
	}

	/**
	 * Read json object.
	 *
	 * @return the json object
	 * @throws JsonException the json exception
	 * @throws IOException   the io exception
	 */
	public JSONObject read() throws JsonException, IOException {
		JsonObject json = (JsonObject) Jsoner.deserialize(new FileReader(file));
		Object[] array = json.values().toArray();
		for (Object o : array) {
			Field[] fields = o.getClass().getFields();

		}
		return null;
	}
}
