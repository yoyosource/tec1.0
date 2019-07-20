package tec.codeexecutor;

import tec.utils.Variable;
import tec.utils.VariableList;

public class ExpressionAdvancedInfo {
	private VariableList info;
	public ExpressionAdvancedInfo() {
		info = new VariableList();
	}
	public ExpressionAdvancedInfo set(InfoID identifier, Object value) {
		if (info.includesVariable(identifier.toString())) {
			info.set(identifier.toString(), value);
		}
		return this;
	}

}
enum InfoID {
	TOKENS("tokens");
	String id;
	InfoID(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}
}