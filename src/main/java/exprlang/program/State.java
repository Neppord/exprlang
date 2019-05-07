package exprlang.program;

public interface State {

    static State empty() {
        return state -> 0;
    }

    Integer lookup(String name);
}
