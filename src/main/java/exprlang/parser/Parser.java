package exprlang.parser;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface Parser<T> {

    static Parser<CharSequence> match(CharSequence... toMatch) {
        return s -> {
            for(CharSequence match: toMatch) {
                int length = match.length();
                if (s.subSequence(0, length).equals(match)) {
                    CharSequence rest = s.subSequence(length, s.length());
                    return new Result<>(match, rest);
                }
            }
            throw new Exception("Expected one of " + Arrays.toString(toMatch) + " got " + s);
        };
    }

    static <T> Parser<T> constant(T value) {
        return s -> new Result<>(value, s);
    }

    Result<T> parse(CharSequence s) throws Exception;

    default Parser<T> or(Parser<T> other) {
        return s -> {
            try{
                return parse(s);
            } catch (Exception e) {
                return other.parse(s);
            }
        };
    }

    default <R> Parser<R> map(Function<T, R> function) {
        return s -> parse(s).map(function);
    }

    default <R> Parser<R> apply(Parser<Function<T, R>> functionParser) {
        return s -> {
            Result<Function<T, R>> functionResult = functionParser.parse(s);
            return this.map(functionResult.value).parse(functionResult.rest);
        };
    }

    static <T, R> Parser<R> apply(Parser<Function<T, R>> function, Parser<T> arg) {
        return arg.apply(function);
    }

    static <T, R> Parser<R> apply(Function<T, R> function, Parser<T> arg) {
        return arg.map(function);
    }

    static <T, U, R> Parser<R> apply(Parser<BiFunction<T, U, R>> function, Parser<T> arg1, Parser<U> arg2) {
        return arg2.apply(arg1.apply(function.map(f -> x -> y -> f.apply(x, y))));
    }

    static <T> Parser<T> apply(BinaryOperator<T> function, Parser<T> arg1, Parser<T> arg2) {
        return arg2.apply(arg1.map(x -> y -> function.apply(x, y)));
    }

    static <T, U, R> Parser<R> apply(BiFunction<T, U, R> function, Parser<T> arg1, Parser<U> arg2) {
        return arg2.apply(arg1.map(x -> y -> function.apply(x, y)));
    }

    default Parser<T> manyR(BinaryOperator<T> op) {
        Parser<T> laizy = s -> manyR(op).parse(s);
        return Parser.apply(op, this, laizy).or(this);
    }

    default Parser<T> chainR(Parser<BinaryOperator<T>> operatorParser) {
        Parser<T> laizy = s -> chainR(operatorParser).parse(s);
        Parser<T> parser = laizy.apply(operatorParser.apply(this.map(arg1 -> op -> arg2 -> op.apply(arg1, arg2))));
        return parser.or(this);
    }

    default Parser<T> drop(Parser<?> toDrop) {
        return apply((x, y) -> x, this, toDrop);
    }
}
