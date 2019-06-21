package tec.codescanner;

/**
 * The type Trimmer manager.
 */
public class TrimmerManager {

    private String text = "";

	/**
	 * Trim.
	 *
	 * @param s the s
	 */
	public void trim(String s) {
        String[] strings = s.split("\n");

        StringBuilder st = new StringBuilder();
        for (String sb : strings) {
            if (sb.trim().length() != 0) {
                st.append(sb.trim() + "\n");
            }
        }
        text = st.toString();
    }

	/**
	 * Gets text.
	 *
	 * @return the text
	 */
	public String getText() {
        return text;
    }
}
