package tec.interfaces;

import tec.codeexecutor.Executor;
import tec.utils.Token;

import java.util.ArrayList;

public interface Statement {
	String getName();

	boolean execute(ArrayList<Token> tokens, Executor executor);

}
