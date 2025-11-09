package splat.parser.elements;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import splat.lexer.Token;
import splat.parser.ParseException;
import splat.parser.elements.BinaryOpExpression;
import splat.parser.elements.UnaryOpExpression;
import splat.semanticanalyzer.SemanticAnalysisException;

public enum Type {

    STRING("String"),
    INTEGER("Integer"),
    BOOLEAN("Boolean"),
    VOID("void");

    private final String name;

    private Type(String name) { this.name = name; }

    private static final List<Type> COMPARABLE_TYPES = Collections.unmodifiableList(
            Arrays.asList(STRING, INTEGER, BOOLEAN)
        );

    private static final List<Type> ORDERED_TYPES = Collections.unmodifiableList(
            Arrays.asList(INTEGER)
        );

    private static final List<Type> ARITHMETIC_TYPES = Collections.unmodifiableList(
            Arrays.asList(INTEGER)
        );

    public static final Type getVarType(Token tok) throws ParseException
    {
        if (tok.getValue().equals("void")) {
            throw new ParseException("Variable type can't be void!", tok);
        }

        return getFuncReturnType(tok);
    }

    public static final Type getFuncReturnType(Token tok)  throws ParseException
    {
        String typeName = tok.getValue();
        for (Type type : Type.values()) {
            if (type.getName().equals(typeName)) {
                return type;
            }
        }

        throw new ParseException("Unknown function return type: " + typeName, tok);
    }

    public static final void verifyComparable(Type LHSType, Type RHSType, BinaryOpExpression binExpr)
            throws SemanticAnalysisException
    {
        if (LHSType != RHSType) {
            throw new SemanticAnalysisException("Can't compare different types!", binExpr);
        }
        if (!COMPARABLE_TYPES.contains(LHSType)) {
            throw new SemanticAnalysisException("Type '" + LHSType + "' is not comparable!", binExpr);
        }
    }

    public static final void verifyOrdered(Type LHSType, Type RHSType, BinaryOpExpression binExpr)
            throws SemanticAnalysisException
    {
        if (LHSType != RHSType) {
            throw new SemanticAnalysisException("Can't order different types!", binExpr);
        }
        if (!ORDERED_TYPES.contains(LHSType)) {
            throw new SemanticAnalysisException("Type '" + LHSType + "' is not ordered!", binExpr);
        }
    }

    public static final void verifyBoolean(Type LHSType, Type RHSType, BinaryOpExpression binExpr)
            throws SemanticAnalysisException
    {
        if (LHSType != RHSType) {
            throw new SemanticAnalysisException(
                    "Can't apply a binary boolean operator on different types!", binExpr);
        }
        if (LHSType != Type.BOOLEAN) {
            throw new SemanticAnalysisException("Type '" + LHSType + "' is not boolean!", binExpr);
        }
    }

    public static final void verifyBoolean(Type type, UnaryOpExpression unExpr)
            throws SemanticAnalysisException
    {
        if (type != Type.BOOLEAN) {
            throw new SemanticAnalysisException("Type '" + type + "' is not boolean!", unExpr);
        }
    }

    public static final void verifyArithmetic(Type LHSType, Type RHSType, BinaryOpExpression binExpr)
            throws SemanticAnalysisException
    {
        if (LHSType != RHSType) {
            throw new SemanticAnalysisException(
                    "Can't apply a binary arithmetical operator on different types!", binExpr);
        }
        if (!ARITHMETIC_TYPES.contains(LHSType)) {
            throw new SemanticAnalysisException("Type '" + LHSType + "' is not arithmetic!", binExpr);
        }
    }

    public static final void verifyArithmetic(Type type, UnaryOpExpression unExpr)
            throws SemanticAnalysisException
    {
        if (!ARITHMETIC_TYPES.contains(type)) {
            throw new SemanticAnalysisException("Type '" + type + "' is not arithmetic!", unExpr);
        }
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
