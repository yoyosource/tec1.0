package calculator;

import java.util.HashMap;

public class History {

    private HashMap<String, Double> history = new HashMap<>();

    public History() {

    }

    public History(String st) {
        this.set(st);
    }

    public History add(String string, Double d) {
        if (!(history.containsKey(string))) {
            history.put(string, d);
        }
        return this;
    }

    public History put(String string, Double d) {
        if (!(history.containsKey(string))) {
            history.put(string, d);
        }
        return this;
    }

    public Double get(String string) {
        return history.get(string);
    }

    public int size() {
        return history.size();
    }

    public boolean hasKey(String key) {
        return history.containsKey(key);
    }

    public boolean contains(String key) {
        return hasKey(key);
    }

    public History merge(History history) {
        for (String key : history.history.keySet()) {
            this.history.put(key, history.get(key));
        }
        return this;
    }

    public History set(String history) {
        String[] histories = history.split(" ");
        for (int i = 1; i < histories.length - 1; i++) {
            String[] hist = histories[i].split(";;;");
            String[] h1 = hist[0].split("=");
            String[] h2 = hist[1].split("=");
            this.put(h1[1], Double.parseDouble(h2[1]));
        }
        return this;
    }

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
