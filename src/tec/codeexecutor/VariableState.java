package tec.codeexecutor;

import java.util.ArrayList;

public class VariableState {

    private ArrayList<Var> vars = new ArrayList<>();

    public VariableState() {

    }

    public boolean addVar(Var var) {
        if (vars.contains(var)) {
            return false;
        }
        vars.add(var);
        return true;
    }

    public boolean setVar(String name, Var var) {
        if (vars.contains(new Var(name, 0, "str"))) {
            vars.get(vars.indexOf(new Var(name, 0, "str"))).setValue(var.getValue(), var.getType());
            return true;
        }
        return false;
    }

    public boolean removeVar(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            vars.remove(new Var(name, 0, "str"));
            return true;
        }
        return false;
    }

    public boolean isVariable(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            return true;
        }
        return false;
    }

    public String getVarType(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            return vars.get(vars.indexOf(new Var(name, 0, "str"))).getType();
        }
        return "";
    }

    public Object getVarValue(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            return vars.get(vars.indexOf(new Var(name, 0, "str"))).getValue();
        }
        return "";
    }

    public ArrayList<Var> getVars() {
        return vars;
    }

}
