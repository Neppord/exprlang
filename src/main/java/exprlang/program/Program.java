package exprlang.program;

public interface Program {

    static Program assigning(String name, Expression expression) {
        return state -> query -> {
            if (query.equals(name)) {
                return expression.evaluate(state);
            } else {
                return state.lookup(query);
            }
        };
    }

    State run(State state);

    default Program before(Program next) {
        return state -> next.run(run(state));
    }
}
