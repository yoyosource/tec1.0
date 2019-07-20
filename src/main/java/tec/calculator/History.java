package tec.calculator;

import java.util.HashMap;

/**
 * The type History.
 */
public class History {

    private HashMap<String, Float> history = new HashMap<>();

	/**
	 * Instantiates a new History.
	 */
	public History() {

    }

	/**
	 * Instantiates a new History.
	 *
	 * @param st the st
	 */
	public History(String st) {
        this.set(st);
    }

	/**
	 * Add history.
	 *
	 * @param string the string
	 * @param d      the d
	 * @return the history
	 */
	public History add(String string, Float d) {
        if (!(history.containsKey(string))) {
            history.put(string, d);
        }
        return this;
    }

	/**
	 * Put history.
	 *
	 * @param string the string
	 * @param d      the d
	 * @return the history
	 */
	public History put(String string, Float d) {
        if (!(history.containsKey(string))) {
            history.put(string, d);
        }
        return this;
    }

	/**
	 * Get float.
	 *
	 * @param string the string
	 * @return the float
	 */
	public Float get(String string) {
        return history.get(string);
    }

	/**
	 * Size int.
	 *
	 * @return the int
	 */
	public int size() {
        return history.size();
    }

	/**
	 * Has key boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public boolean hasKey(String key) {
        return history.containsKey(key);
    }

	/**
	 * Contains boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public boolean contains(String key) {
        return hasKey(key);
    }

	/**
	 * Merge history.
	 *
	 * @param history the history
	 * @return the history
	 */
	public History merge(History history) {
        for (String key : history.history.keySet()) {
            this.history.put(key, history.get(key));
        }
        return this;
    }

	/**
	 * Set history.
	 *
	 * @param history the history
	 * @return the history
	 */
	public History set(String history) {
        String[] histories = history.split(" ");
        for (int i = 1; i < histories.length - 1; i++) {
            String[] hist = histories[i].split(";;;");
            String[] h1 = hist[0].split("=");
            String[] h2 = hist[1].split("=");
            this.put(h1[1], Float.parseFloat(h2[1]));
        }
        return this;
    }

	/**
	 * Print.
	 */
	public void print() {
        for (String st : history.keySet()) {
            System.out.println("- " + st + " = " + history.get(st));
        }
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        st.append("History[ ");
        for (String s : history.keySet()) {
            st.append("key=" + s + ";;;d=" + history.get(s) + " ");
        }
        st.append("]");
        return st.toString();
    }

}
