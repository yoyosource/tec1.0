package utils;

public class Token {

    private String key = "";
    private Object val;

    public Token(String key, Object value) {
        this.key = key;
        this.val = value;
    }

    public String getKey() {
        return key;
    }

    public Object getVal() {
        return val;
    }

    public boolean isType() {
        if (key.equals("str") || key.equals("num") || key.equals("int") || key.equals("bol") || key.equals("chr")) {
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "Token{" +
                "key='" + key + '\'' +
                ", val='" + val + "'" +
                '}';
    }

}
