package exprlang;

import exprlang.parser.Parser;
import exprlang.program.Expression;
import exprlang.program.Program;
import exprlang.program.State;

import java.util.Objects;
import java.util.function.BinaryOperator;

public class Main {
    public static void main(String... argv) throws Exception {
        Parser<BinaryOperator<Expression>> plus = Parser
            .match("+")
            .map( _1 -> Expression::add);

        Parser<BinaryOperator<Expression>> minus = Parser
            .match("-")
            .map( _1 -> Expression::subtract);
        Parser<BinaryOperator<Expression>> multiply = Parser
            .match("*")
            .map( _1 -> Expression::multiply);
        Parser<CharSequence> equals = Parser
            .match("=");

        Parser<String> identifier = Parser.match("x", "y", "z")
            .map(CharSequence::toString);

        Parser<Expression> digit = Parser
            .match("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            .map(CharSequence::toString)
            .manyR(String::concat)
            .map(Integer::parseInt)
            .map(Expression::constant);

        Parser<Expression> variable = identifier.map(Expression::variable);
        Parser<Expression> expression = digit.or(variable)
            .chainR(multiply)
            .chainL(plus.or(minus));

        Parser<Program> assignment = Parser
            .apply(Program::assigning, identifier.drop(equals), expression);
        Parser<BinaryOperator<Program>> newline = Parser
            .match("\n")
            .map(_1 -> Program::before);
        Parser<Program> programParser = assignment.chainR(newline);

        String sourceCode =
            "x=10\n" +
            "y=9+3*2+x-1-2+2*x"
        ;
        Program program = programParser.parse(sourceCode).value;
        System.out.println(program.run(State.empty()).lookup("y"));
    }
}
