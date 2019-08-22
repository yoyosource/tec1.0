package test;

import tec.codeexecutor.Calculator;
import tec.codeexecutor.Lexer;

public class CalculatorTest {

    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        lexer.createTokens(System.currentTimeMillis() + "-" + (System.currentTimeMillis() + 1000), false);

        Calculator calculator1 = new tec.codeexecutor.Calculator(lexer.getTokens());
        calculator1.calculate();
    }

}
