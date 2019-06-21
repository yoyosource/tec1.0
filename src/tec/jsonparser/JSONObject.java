package tec.jsonparser;

import tec.exceptions.DefinitonException;
import tec.utils.Variable;

import java.util.ArrayList;

/**
 * The type Json object.
 */
@SuppressWarnings("ALL")
public class JSONObject extends Object {
	private ArrayList<Variable<Object>> obj;

	/**
	 * Instantiates a new Json object.
	 */
	public JSONObject() {
		this.obj = new ArrayList<Variable<Object>>();
	}

	/**
	 * Instantiates a new Json object.
	 *
	 * @param object the object
	 */
	public JSONObject(ArrayList<Variable<Object>> object) {
		this.obj = object;
	}

	/**
	 * Add.
	 *
	 * @param obj the obj
	 * @throws DefinitonException the definiton exception
	 */
	public void add(Variable<Object> obj) throws DefinitonException {
		for (Variable<Object> current : this.obj) {
			if (current.getIdentifier().equals(obj.getIdentifier())) {
				throw new DefinitonException(obj.getIdentifier() + " can't be defined multiple times!");
			}
		}
		this.obj.add(obj);
	}

	/**
	 * Add.
	 *
	 * @param identifier the identifier
	 * @param value      the value
	 * @throws DefinitonException the definiton exception
	 */
	public void add(String identifier, Object value) throws DefinitonException {
		this.add(new Variable<Object>(identifier, value));
	}

	/**
	 * Set.
	 *
	 * @param obj the obj
	 * @throws DefinitonException the definiton exception
	 */
	public void set(Variable<Object> obj) throws DefinitonException {
		for (Variable<Object> current : this.obj) {
			if (current.getIdentifier().equals(obj.getIdentifier())) {
				current.setValue(obj.getValue());
			} else {
				this.add(obj);
			}
		}
	}

	/**
	 * Set.
	 *
	 * @param identifier the identifier
	 * @param value      the value
	 * @throws DefinitonException the definiton exception
	 */
	public void set(String identifier, Object value) throws DefinitonException {
		this.set(new Variable<Object>(identifier, value));
	}

	/**
	 * Get variable.
	 *
	 * @param identifier the identifier
	 * @return the variable
	 * @throws DefinitonException the definiton exception
	 */
	public Variable<Object> get(String identifier) throws DefinitonException {
		for (Variable<Object> current : obj) {
			if (current.getIdentifier().equals(identifier)) {
				return current;
			}
		}
		throw new DefinitonException(identifier + " is not defined!");
	}
}
