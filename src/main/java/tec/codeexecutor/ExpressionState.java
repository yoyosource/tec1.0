package tec.codeexecutor;

import tec.utils.Token;

import java.util.ArrayList;
import java.util.List;

public class ExpressionState {

    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        this.errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }


    private Token result = null;

    public void setResult(Token token) {
        this.result = token;
    }

    public Token getResult() {
        return result;
    }


    private List<Long> times = new ArrayList<>();

    public void addTime(long l) {
        times.add(l);
    }

    public List<Long> getTimes() {
        return times;
    }

    public long getTime() {
        long time = 0;
        for (long l : times) {
            time += l;
        }
        return time;
    }


    @Override
    public String toString() {
        return "ExpressionState{" +
                "errors=" + errors +
                ", result=" + result +
                ", times=" + times +
                '}';
    }

}
