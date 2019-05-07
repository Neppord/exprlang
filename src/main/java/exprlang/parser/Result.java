package exprlang.parser;

import java.util.function.Function;

public class Result<T> {
    public final T value;
    public final CharSequence rest;

    public Result(T value, CharSequence rest) {
        this.value = value;
        this.rest = rest;
    }

    public <R> Result<R> map(Function<T, R> function) {
        return new Result<>(function.apply(value), rest);
    }
}
