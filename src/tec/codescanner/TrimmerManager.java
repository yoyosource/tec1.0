package tec.codescanner;

public class TrimmerManager {

    private String text = "";

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

    public String getText() {
        return text;
    }
}
