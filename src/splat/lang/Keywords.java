package splat.lang;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class Keywords {
    public static final List<String> RESERVED_WORDS = Collections.unmodifiableList(
            Arrays.asList(
                "while", "if", "else", "true", "false", "and",  "or",  ">",  
                "<",  "==",  ">=",  "<=",  "+",  "-",  "*",  "/",  "%", "not",
                "Integer", "Boolean", "String", "void", "return", "print", 
                "print_line", "then", "end", "program", "begin", "is", "do"
            )
        );
}
