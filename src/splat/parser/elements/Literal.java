package splat.parser.elements;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;

import splat.lexer.Token;
import splat.parser.ParseException;

public class Literal extends Expression {
    private String value;

    public static final List<String> BOOLEAN_LITERALS = Collections.unmodifiableList(
            new ArrayList<String>(Arrays.asList("true", "false"))
        );

    public Literal(Token tok) throws ParseException {
        super(tok);

        char firstChar = tok.getValue().charAt(0);
        if (firstChar == '"') { // string literal start
            verifyStringLiteral(tok);
        } else if (Character.isDigit(firstChar)) { // integer literal start
            verifyIntegerLiteral(tok);
        } else {
            verifyBooleanLiteral(tok);
        }

        this.value = tok.getValue();
    }

    public static void verifyIntegerLiteral(Token tok) throws ParseException {
        String value = tok.getValue();
        if (value.charAt(0) == '0' && value.length() > 1) {
            throw new ParseException("Invalid integer literal: " + value, tok);
        }

        try{
            Integer.parseInt(value);
        } catch(NumberFormatException e) {
            throw new ParseException("Invalid integer literal: " + value, tok);
        }
    }

    public static void verifyStringLiteral(Token tok) throws ParseException {
        String value = tok.getValue();
        if (value.length() < 2) {
            throw new ParseException("Invalid string literal: " + value, tok);
        }

        for (int i = 1; i < value.length()-1; i++) {
            if (value.charAt(i) == '"') {
                throw new ParseException("String literal contains '\"': " + value, tok);
            }
            if (value.charAt(i) == '\\') {
                // TODO: Should we consider escape characters???
                throw new ParseException("String literal contains '\\': " + value, tok);
            }
            if (value.charAt(i) == '\n') {
                throw new ParseException("String literal contains '\n': " + value, tok);
            }
        }
    }

    public static void verifyBooleanLiteral(Token tok) throws ParseException {
        String value = tok.getValue();
        if (!BOOLEAN_LITERALS.contains(value)) {
            throw new ParseException("Invalid boolean literal: " + value, tok);
        }
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "{value: " + this.value + "}";
    }
}
