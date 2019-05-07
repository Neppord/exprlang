package exprlang.program;

import static exprlang.program.Expression.constant;
import static exprlang.program.Expression.variable;
import static exprlang.program.Program.assigning;

public class Main {

    public static void main(String[] argv) {
        Program program =
            assigning("a", constant(10).add(constant(2)))
            .before(assigning("b", constant(10).multiply(variable("a"))));
        State result = program.run(State.empty());
        System.out.println("a = 10 + 2 = " + result.lookup("a"));
        System.out.println("b = 10 * a = " + result.lookup("b"));
    }
}
