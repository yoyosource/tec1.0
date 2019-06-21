package tec.utils;

/**
 * The type Variable.
 *
 * @param <T> the type parameter
 */
public class Variable<T> {
	private String identifier;
	private T value;

	/**
	 * Instantiates a new Variable.
	 *
	 * @param identifier the identifier
	 * @param value      the value
	 */
	public Variable(String identifier, T value) {
		this.identifier = identifier;
		this.value = value;
	}

	/**
	 * Gets identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets value.
	 *
	 * @param value the value
	 */
	public void setValue(T value) {
		this.value = value;
	}
}
