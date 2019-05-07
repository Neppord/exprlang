package exprlang.program;

public interface Expression {
    Integer evaluate(State state);

    static Expression constant(Integer n) {
        return _1 -> n;
    }

    static Expression variable(String name) {
        return state -> state.lookup(name);
    }

    default Expression multiply(Expression other) {
        return state -> evaluate(state) * other.evaluate(state);
    }

    default Expression add(Expression other) {
        return state -> evaluate(state) + other.evaluate(state);
    }

    default Expression subtract(Expression other) {
        return state -> evaluate(state) - other.evaluate(state);
    }
}
