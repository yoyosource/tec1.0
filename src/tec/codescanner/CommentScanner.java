package codescanner;

import java.util.ArrayList;

public class CommentScanner {

    private ArrayList<String> code = new ArrayList<>();

    public void removeComments(String s) {
        String[] code = s.split("\n");
        ArrayList<String> newCode = new ArrayList<>();
        int inComment = 0;
        for (String st : code) {
            if (st.startsWith("/*")) {
                inComment++;
            }
            if (!(st.startsWith("//")) && inComment == 0) {
                newCode.add(st);
            }
            if (st.endsWith("*/")) {
                inComment--;
            }
            if (inComment < 0) {
                inComment = 0;
            }

        }
        code = new String[newCode.size()];
        for (int i = 0; i < newCode.size(); i++) {
            code[i] = newCode.get(i);
        }
        this.code = newCode;
    }

    public ArrayList<String> getCode() {
        return code;
    }

}
