package tec.codeexecutor;

import java.util.Objects;

/**
 * The type Var.
 */
public class Var {

    private String name;
    private Object value;
    private String type;

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
        if (this.type.equals(type)) {
            this.value = value;
        }
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
