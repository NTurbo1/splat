package splat.lang;

import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Operations {
    public static final Map<String, String> BINARY_OPERATORS = Collections.unmodifiableMap(
            Stream
                .of(new Object[][] {
                    {"and", "and"}, 
                    {"or", "or"}, 
                    {"biggerThan", ">"},
                    {"lessThan", "<"},
                    {"equal", "=="},
                    {"biggerThanOrEqual", ">="},
                    {"lessThanOrEqual", "<="},
                    {"add", "+"},
                    {"subtraction", "-"},
                    {"multiplication", "*"},
                    {"division", "/"},
                    {"modulo", "%"},
                })
                .collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]))
        );

    public static final Map<String, String> UNARY_OPERATORS = Collections.unmodifiableMap(
            Stream
                .of(new Object[][] {
                    {"not", "not"}, 
                    {"negative", "-"}, 
                })
                .collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]))
        );
}
