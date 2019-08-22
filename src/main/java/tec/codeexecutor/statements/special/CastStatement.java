package tec.codeexecutor.statements.special;

import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.Var;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class CastStatement implements Statement {

    @Override
    public String getName() {
        return "cast";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {

        if (!tokens.get(0).getKey().equals("typ")) {
            return false;
        }

        String typeTo = tokens.get(0).getVal().toString();
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

        String typeOf = expression.getResult().getResult().getKey();

        if (typeOf.equals(typeTo)) {
            variableState.addVar(new Var(varName, expression.getResult().getResult().getVal(), typeTo));
            return true;
        }

        if (typeTo.equals("str")) {
            variableState.addVar(new Var(varName, expression.getResult().getResult().getVal() + "", typeTo));
            return true;
        }

        if (typeOf.equals("str")) {
            if (typeTo.equals("chr")) {
                if ((expression.getResult().getResult().getVal() + "").length() > 0) {
                    char c = (expression.getResult().getResult().getVal() + "").toCharArray()[0];
                    variableState.addVar(new Var(varName, c, typeTo));
                } else {
                    variableState.addVar(new Var(varName,"", typeTo));
                }
            }
            if (typeTo.equals("bol")) {
                if ((expression.getResult().getResult().getVal() + "").equalsIgnoreCase("true")) {
                    variableState.addVar(new Var(varName, true, typeTo));
                } else {
                    variableState.addVar(new Var(varName, false, typeTo));
                }
            }
            if (typeTo.equals("int")) {
                if (expression.getResult().getResult().getVal().toString().startsWith("##")) {
                    variableState.addVar(new Var(varName, Integer.parseInt(expression.getResult().getResult().getVal().toString().substring(2), 16), typeTo));
                    return true;
                }
                variableState.addVar(new Var(varName, Integer.parseInt(expression.getResult().getResult().getVal() + ""), typeTo));
            }
            if (typeTo.equals("num")) {
                variableState.addVar(new Var(varName, Double.parseDouble(expression.getResult().getResult().getVal() + ""), typeTo));
            }
            return true;
        }
        if (typeOf.equals("chr")) {
            if (typeTo.equals("bol")) {
                if ((expression.getResult().getResult().getVal() + "").equals("1")) {
                    variableState.addVar(new Var(varName, true, typeTo));
                } else {
                    variableState.addVar(new Var(varName, false, typeTo));
                }
            }
            if (typeTo.equals("int") || typeTo.equals("num")) {
                String s = expression.getResult().getResult().getVal() + "";
                if (s.length() > 0) {
                    int i = (int) s.toCharArray()[0];
                    variableState.addVar(new Var(varName, i, typeTo));
                } else {
                    variableState.addVar(new Var(varName, 0, typeTo));
                }
            }
            return true;
        }
        if (typeOf.equals("num")) {
            float i = (float)expression.getResult().getResult().getVal();
            if (typeTo.equals("bol")) {
                if (i <= 0) {
                    variableState.addVar(new Var(varName, false, typeTo));
                } else {
                    variableState.addVar(new Var(varName, true, typeTo));
                }
            }
            if (typeTo.equals("int")) {
                variableState.addVar(new Var(varName, (int)i, typeTo));
            }
            if (typeTo.equals("chr")) {
                variableState.addVar(new Var(varName, (char)(int)i, typeTo));
            }
            return true;
        }
        if (typeOf.equals("int")) {
            int i = (int)expression.getResult().getResult().getVal();
            if (typeTo.equals("bol")) {
                if (i <= 0) {
                    variableState.addVar(new Var(varName, false, typeTo));
                } else {
                    variableState.addVar(new Var(varName, true, typeTo));
                }
            }
            if (typeTo.equals("num")) {
                variableState.addVar(new Var(varName, (float)i, typeTo));
            }
            if (typeTo.equals("chr")) {
                variableState.addVar(new Var(varName, (char)i, typeTo));
            }
            return true;
        }
        if (typeOf.equals("bol")) {
            boolean i = (boolean)expression.getResult().getResult().getVal();
            if (typeTo.equals("int")) {
                if (i) {
                    variableState.addVar(new Var(varName, 1, typeTo));
                } else {
                    variableState.addVar(new Var(varName, 0, typeTo));
                }
            }
            if (typeTo.equals("num")) {
                if (i) {
                    variableState.addVar(new Var(varName, 1.0, typeTo));
                } else {
                    variableState.addVar(new Var(varName, 0.0, typeTo));
                }
            }
            if (typeTo.equals("chr")) {
                if (i) {
                    variableState.addVar(new Var(varName, '1', typeTo));
                } else {
                    variableState.addVar(new Var(varName, '0', typeTo));
                }
            }
            return true;
        }

        return false;
    }

}
