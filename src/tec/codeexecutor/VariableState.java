package tec.codeexecutor;

import java.util.ArrayList;

/**
 * The type Variable state.
 */
public class VariableState {

    private ArrayList<Var> vars = new ArrayList<>();

    /**
     * Instantiates a new Variable state.
     */
    public VariableState() {

    }

    /**
     * Add var boolean.
     *
     * @param var the var
     * @return the boolean
     */
    public boolean addVar(Var var) {
        if (vars.contains(var)) {
            return false;
        }
        vars.add(var);
        return true;
    }

    /**
     * Sets var.
     *
     * @param name the name
     * @param var  the var
     * @return the var
     */
    public boolean setVar(String name, Var var) {
        if (vars.contains(new Var(name, 0, "str"))) {
            vars.get(vars.indexOf(new Var(name, 0, "str"))).setValue(var.getValue(), var.getType());
            return true;
        }
        return false;
    }

    /**
     * Remove var boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean removeVar(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            vars.remove(new Var(name, 0, "str"));
            return true;
        }
        return false;
    }

    /**
     * Is variable boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean isVariable(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            return true;
        }
        return false;
    }

    /**
     * Gets var type.
     *
     * @param name the name
     * @return the var type
     */
    public String getVarType(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            return vars.get(vars.indexOf(new Var(name, 0, "str"))).getType();
        }
        return "";
    }

    /**
     * Gets var value.
     *
     * @param name the name
     * @return the var value
     */
    public Object getVarValue(String name) {
        if (vars.contains(new Var(name, 0, "str"))) {
            return vars.get(vars.indexOf(new Var(name, 0, "str"))).getValue();
        }
        return "";
    }

    /**
     * Gets vars.
     *
     * @return the vars
     */
    public ArrayList<Var> getVars() {
        return vars;
    }

}
