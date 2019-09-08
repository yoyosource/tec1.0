package tec.codeexecutor;

import tec.exceptions.AccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Var.
 */
@SuppressWarnings("unchecked")
public class Var {

    private String name;
    private Object value;
    private String type;

    private boolean constant = false;

    private boolean array = false;

	/**
	 * Instantiates a new Var.
	 *
	 * @param name  the name
	 * @param value the value
	 * @param type  the type
	 */
	public Var(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public void setConstant() {
	    constant = true;
    }

    public void setArray() { array = true; }

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
        return name;
    }

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public Object getValue() {
        return value;
    }

    public List<Object> getValueAsArray() {
	    if (!array) {
            throw new AccessException("This variable is not an array");
        }
        if (!(value instanceof ArrayList)) {
            throw new AccessException("This variable is not of type Array but was changed to array beforehand");
        }
        return ((List<Object>)value);
    }

    public Object getValue(int index) {
	    if (!array) {
	        throw new AccessException("This variable is not an array");
        }
	    if (!(value instanceof ArrayList)) {
	        throw new AccessException("This variable is not of type Array but was changed to array beforehand");
        }
        return ((List<Object>)value).get(index);
    }

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public String getType() {
        return type;
    }

	/**
	 * Sets value.
	 *
	 * @param value the value
	 * @param type  the type
	 */
	public void setValue(Object value, String type) {
	    if (constant) {
	        return;
        }

        if (this.type.equals(type) || (this.type.equals("num") && type.equals("int"))) {
            this.value = value;
        }
        if (this.type.equals("int") && type.equals("num")) {
            this.value = (Integer)value;
        }
    }

    public void addValue(Object value, String type) {
        if (constant) {
            return;
        }

        if (!array) {
            return;
        }

        if (this.type.equals(type) || (this.type.equals("num") && type.equals("int"))) {
            if (!(this.value instanceof ArrayList)) {
                throw new AccessException("This variable is not of type Array but was changed to array beforehand");
            }
            if (value instanceof ArrayList) {
                throw new AccessException("You cannot add an Array to an Array");
            }
            ((List<Object>)this.value).add(value);
        }
        if (this.type.equals("int") && type.equals("num")) {
            if (!(this.value instanceof ArrayList)) {
                throw new AccessException("This variable is not of type Array but was changed to array beforehand");
            }
            if (value instanceof ArrayList) {
                throw new AccessException("You cannot add an Array to an Array");
            }
            ((List<Object>)this.value).add(value);
        }
    }

    public void setValue(Object value, String type, int index) {

    }

    public void removeValue(int index) {

    }

    public void clear() {
	    if (!array) {
	        throw new AccessException("This variable is not of type Array");
        }
	    if (!(value instanceof ArrayList)) {
	        throw new AccessException("You cannot clear a not Array variable");
        }
        ((List<Object>)value).clear();
    }

    public boolean isArray() {
        return array;
    }

    public boolean isConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return "Var{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
