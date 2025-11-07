package splat.lang;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

import splat.lexer.Token;
import splat.parser.ParseException;

public class Types {
    public static final List<String> STANDARD_TYPES = Collections.unmodifiableList(
            Arrays.asList("Integer", "Boolean", "String")
        );

    public static final String VOID_TYPE = "void";

    public static void verifyVarType(Token tok) throws ParseException {
        String tokValue = tok.getValue();
        if (!STANDARD_TYPES.contains(tokValue)) {
            throw new ParseException("Unknown variable type: " + tokValue, tok);
        }
    }

    public static void verifyFuncReturnType(Token tok) throws ParseException {
        String tokValue = tok.getValue();
        if (!STANDARD_TYPES.contains(tokValue) && !VOID_TYPE.equals(tokValue)) {
            throw new ParseException("Unknown function return type: " + tokValue, tok);
        }
    }
}
