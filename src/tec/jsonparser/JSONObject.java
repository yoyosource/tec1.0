package tec.jsonparser;

import tec.exceptions.DefinitonException;

import java.util.ArrayList;

@SuppressWarnings("Convert2Diamond")
public class JSONObject {
	private ArrayList<Variable<Object>> obj;
	public JSONObject() {
		this.obj = new ArrayList<Variable<Object>>();
	}
	public JSONObject(ArrayList<Variable<Object>> object) {
		this.obj = object;
	}
	public void add(Variable<Object> obj) throws DefinitonException {
		for (Variable<Object> current : this.obj) {
			if (current.getIdentifier().equals(obj.getIdentifier())) {
				throw new DefinitonException(obj.getIdentifier() + " can't be defined multiple times!");
			}
		}
		this.obj.add(obj);
	}
	public void add(String identifier, Object value) throws DefinitonException {
		this.add(new Variable<Object>(identifier, value));
	}
	public void set(Variable<Object> obj) throws DefinitonException {
		for (Variable<Object> current : this.obj) {
			if (current.getIdentifier().equals(obj.getIdentifier())) {
				current.setValue(obj.getValue());
			} else {
				this.add(obj);
			}
		}
	}
	public void set(String identifier, Object value) throws DefinitonException {
		this.set(new Variable<Object>(identifier, value));
	}
}
