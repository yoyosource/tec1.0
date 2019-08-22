package tec.codeexecutor.statements.special;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class HashStatement implements Statement {

    @Override
    public String getName() {
        return "hash";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {

        if (!tokens.get(0).getKey().equals("COD")) {
            return false;
        }

        String type = tokens.get(0).getVal().toString();
        tokens.remove(0);

        if (!tokens.get(0).getKey().equals("COD")) {
            return false;
        }

        String varName = tokens.get(0).getVal().toString();
        tokens.remove(0);

        Expression expression = new Expression(tokens, variableState, executor);
        expression.build();

        if (!executor.runExpressionInfo(expression)) {
            return false;
        }

        try {
            if (type.equals("MD5")) {

            } else if (type.equals("SHA1")) {
                type = type.replaceAll("SHA", "SHA-");
            } else if (type.equals("SHA256")) {
                type = type.replaceAll("SHA", "SHA-");
            } else {
                return false;
            }
            MessageDigest digest = MessageDigest.getInstance(type);
            byte[] hash = digest.digest((expression.getResult().getResult().getVal() + "").getBytes("UTF-8"));
            String hex = toHex(hash).toLowerCase();
            variableState.addVar(new Var(varName, hex, "str"));
        } catch (NoSuchAlgorithmException | IOException e) {
            return false;
        }

        return true;
    }

    public String toHex(byte[] bytes) {
        StringBuilder st = new StringBuilder();
        for (byte b : bytes) {
            String s = String.format("%02X", b);
            st.append(s);
        }
        return st.toString();
    }

}
