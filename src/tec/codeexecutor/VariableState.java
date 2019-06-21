package tec.codeexecutor;

import java.util.ArrayList;

public class VariableState {

    private ArrayList<Var> vars = new ArrayList<>();

    public VariableState() {

    }

    public boolean contains(String name) {
        for (Var var : vars) {
            if (var.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(String name) {
        for (int i = 0; i < vars.size(); i++) {
            if (vars.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public boolean addVar(Var var) {
        if (contains(var.getName())) {
            return false;
        }
        vars.add(var);
        return true;
    }

    public boolean setVar(String name, Var var) {
        if (contains(name)) {
            vars.get(indexOf(name)).setValue(var.getValue(), var.getType());
            return true;
        }
        return false;
    }

    public boolean removeVar(String name) {
        if (contains(name)) {
            vars.remove(new Var(name, 0, "str"));
            return true;
        }
        return false;
    }

    public boolean isVariable(String name) {
        if (contains(name)) {
            return true;
        }
        return false;
    }

    public String getVarType(String name) {
        if (contains(name)) {
            return vars.get(indexOf(name)).getType();
        }
        return "";
    }

    public Object getVarValue(String name) {
        if (contains(name)) {
            return vars.get(indexOf(name)).getValue();
        }
        return "";
    }

    public ArrayList<Var> getVars() {
        return vars;
    }

}
