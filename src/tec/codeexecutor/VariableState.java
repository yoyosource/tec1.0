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
	 * Contains boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public boolean contains(String name) {
        for (Var var : vars) {
            if (var.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Index of int.
	 *
	 * @param name the name
	 * @return the int
	 */
	public int indexOf(String name) {
        for (int i = 0; i < vars.size(); i++) {
            if (vars.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

	/**
	 * Add var boolean.
	 *
	 * @param var the var
	 * @return the boolean
	 */
	public boolean addVar(Var var) {
        if (contains(var.getName())) {
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
        if (contains(name)) {
            vars.get(indexOf(name)).setValue(var.getValue(), var.getType());
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
        if (contains(name)) {
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
        if (contains(name)) {
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
        if (contains(name)) {
            return vars.get(indexOf(name)).getType();
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
        if (contains(name)) {
            return vars.get(indexOf(name)).getValue();
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
