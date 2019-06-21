package tec.utils;

import tec.exceptions.DefinitonException;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class VariableList extends ArrayList<Variable<Object>> {
	public VariableList() {
		super();
	}

	public VariableList(ArrayList<Variable<Object>> object) {
		super(object);
	}
	public boolean includesVariable(String id) {
		for (Variable var : this) {
			if (var.getIdentifier().equals(id)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Set.
	 *
	 * @param obj the obj
	 */
	public void set(Variable<Object> obj) {
		for (Variable<Object> current : this) {
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
	 */
	public void set(String identifier, Object value)  {
		this.set(new Variable(identifier, value));
	}

	/**
	 * Get variable.
	 *
	 * @param identifier the identifier
	 * @return the variable
	 */
	public Variable<Object> get(String identifier) throws DefinitonException {
		for (Variable<Object> current : this) {
			if (current.getIdentifier().equals(identifier)) {
				return current;
			}
		}
		throw new DefinitonException(identifier + " is not defined!");
	}
}
