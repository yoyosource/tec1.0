package tec.jsonparser;

import com.github.cliftonlabs.json_simple.JsonArray;

public class JSONArray extends JsonArray {
	public String join(String delimeter) {
		String s = "";
		for (Object obj : this) {
			s += obj;
			if (!this.get(this.size() - 1).equals(obj)) {
				s += delimeter;
			}
		}
		return s;
	}
}
