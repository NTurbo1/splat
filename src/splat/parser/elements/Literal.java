package splat.parser.elements;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

import splat.lexer.Token;
import splat.parser.ParseException;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.Value;
import splat.executor.StringValue;
import splat.executor.IntegerValue;
import splat.executor.BoolValue;
import splat.executor.ExecutionException;
import splat.executor.ScopeEnvironment;

public class Literal extends Expression {
    private Type type;
    private String value;

    public static final List<String> BOOLEAN_LITERALS = Collections.unmodifiableList(
            new ArrayList<String>(Arrays.asList("true", "false"))
        );

    public Literal(Token tok) throws ParseException {
        super(tok);

        char firstChar = tok.getValue().charAt(0);
        if (firstChar == '"') { // string literal start
            verifyStringLiteral(tok);
            this.type = Type.STRING;
        } else if (Character.isDigit(firstChar)) { // integer literal start
            verifyIntegerLiteral(tok);
            this.type = Type.INTEGER;
        } else {
            verifyBooleanLiteral(tok);
            this.type = Type.BOOLEAN;
        }

        this.value = tok.getValue();
    }
    
    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) 
        throws SemanticAnalysisException
    {
        return this.type;
    }

    @Override
    public Value evaluate(
            Map<String, FunctionDecl> funcMap,
            Map<String, Value> varAndParamMap,
            Stack<ScopeEnvironment> callStack) throws ExecutionException
    {
        switch (this.type)
        {
            case STRING:
                return new StringValue(this.value);
            case INTEGER:
                try {
                    int res = Integer.parseInt(this.value);
                    return new IntegerValue(res);
                } catch (NumberFormatException ex) {
                    throw new ExecutionException("Wrong integer format: " + this.value, this);
                }
            case BOOLEAN:
                boolean res = Boolean.parseBoolean(this.value);
                return new BoolValue(res);
            default:
                throw new ExecutionException(
                    "Literal '" + this.value + "' has unknown type: " + this.type.toString() +
                    ". Man, go fix your semantic analyzer!",
                    this
                );
        }
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
