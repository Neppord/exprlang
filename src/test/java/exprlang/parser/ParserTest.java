package exprlang.parser;

import org.junit.Test;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void constantParser() throws Exception {
        Parser<Integer> parser = Parser.constant(1);
        assertThat(parser.parse("").value, equalTo(1));
    }

    @Test
    public void matchReturnsMatchedInput() throws Exception {
        Parser<CharSequence> parser = Parser.match("1", "2");

        assertThat(parser.parse("1").value, equalTo("1"));
        assertThat(parser.parse("1 ").value, equalTo("1"));
        assertThat(parser.parse("2").value, equalTo("2"));
    }
    @Test
    public void matchConsumesInputAndLeavesNonMatched() throws Exception {
        Parser<CharSequence> parser = Parser.match("1", "2");

        assertThat(parser.parse("1").rest, equalTo(""));
        assertThat(parser.parse("1 ").rest, equalTo(" "));
        assertThat(parser.parse("2").rest, equalTo(""));
    }

    @Test
    public void matchedThrowsExceptionWhenNoMatchFound() {
        try {
            Parser<CharSequence> parser = Parser.match("1", "2");
            parser.parse("3");
        } catch (Exception e) {
            return;
        }
        fail();
    }

    @Test
    public void functor() throws Exception {
        //TODO functor
        Function<CharSequence, Integer> parseInt = n -> Integer.parseInt(n.toString());

        Parser<Integer> parser = Parser.match("1").map(parseInt);

        assertThat(parser.parse("1").value, equalTo(1));
    }

    @Test
    public void applicative() throws Exception {
        //TODO applicative
        Function<CharSequence, Integer> parseInt = n -> Integer.parseInt(n.toString());
        Parser<Function<CharSequence, Integer>> functionParser = Parser.constant(parseInt);

        Parser<Integer> parser = Parser.match("1").apply(functionParser);

        assertThat(parser.parse("1").value, equalTo(1));
    }

    @Test
    public void alternative() throws Exception {
        //TODO alternative
        Parser<CharSequence> parser = Parser.match("1").or(Parser.match("2"));

        assertThat(parser.parse("1").value, equalTo("1"));
        assertThat(parser.parse("2").value, equalTo("2"));
    }

    @Test
    public void manyR() throws Exception {
        Parser<String> digit = Parser
            .match("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            .map(CharSequence::toString);
        Parser<Integer> number = digit.manyR(String::concat).map(Integer::parseInt);

        assertThat(number.parse("11").value, equalTo(11));
    }

    @Test
    public void chainR() throws Exception {
        Parser<BinaryOperator<Integer>> plus = Parser
            .match("+")
            .map( _1 -> (x, y) -> x  + y);
        Parser<String> digit = Parser
            .match("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            .map(CharSequence::toString);
        Parser<Integer> number = digit.manyR(String::concat).map(Integer::parseInt);

        Parser<Integer> expr = number.chainR(plus);

        assertThat(expr.parse("10+1").value, equalTo(11));
    }

    @Test
    public void chainl() throws Exception {
        Parser<BinaryOperator<Integer>> minus = Parser
            .match("-")
            .map( _1 -> (x, y) -> x  - y);
        Parser<String> digit = Parser
            .match("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
            .map(CharSequence::toString);
        Parser<Integer> number = digit.manyR(String::concat).map(Integer::parseInt);

        Parser<Integer> expr = number.chainL(minus);

        assertThat(expr.parse("10-1-2").value, equalTo(7));
    }
}