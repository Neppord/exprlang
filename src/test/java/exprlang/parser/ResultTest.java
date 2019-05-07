package exprlang.parser;

import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class ResultTest {

    @Test
    public void functorInterface() {
        Result<Integer> result = new Result<>(10, "sequence");
        Function<Integer, Integer> inc = n -> n + 1;
        Result<Integer> mapped = result.map(inc);
        assertThat(mapped.rest, equalTo(result.rest));
        assertThat(mapped.value, equalTo(inc.apply(result.value)));
    }
}