package splat.lang;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;

import splat.semanticanalyzer.SemanticAnalysisException;
import splat.parser.elements.BinaryOpExpression;
import splat.parser.elements.UnaryOpExpression;
import splat.parser.elements.Type;

public class Operations {
    public static final Set<String> BINARY_OPERATORS = Collections.unmodifiableSet(
            new HashSet<String>(
                Arrays.asList("and", "or", ">", "<", "==", ">=", "<=", "+", "-", "*", "/", "%")
            )
        );

    public static final Set<String> UNARY_OPERATORS = Collections.unmodifiableSet(
            new HashSet<String>(
                Arrays.asList("not", "-")
            )
        );

    public static final Set<String> ORDER_OPERATORS = Collections.unmodifiableSet(
            new HashSet<String>(
                Arrays.asList("<", ">", "<=", ">=")
            )
        );

    public static final Set<String> BOOLEAN_OPERATORS = Collections.unmodifiableSet(
            new HashSet<String>(
                Arrays.asList("and", "or", "not")
            )
        );

    public static final Set<String> ARITHMETIC_OPERATORS = Collections.unmodifiableSet(
            new HashSet<String>(
                Arrays.asList("+", "-", "*", "/", "%")
            )
        );

    public static final Type verifyBinaryOperation(
            Type LHSType, Type RHSType, BinaryOpExpression binExpr
        ) throws SemanticAnalysisException
    {
        String binOp = binExpr.getOperator();
        if (!BINARY_OPERATORS.contains(binOp)) {
            throw new SemanticAnalysisException("Unknown binary operator: " + binOp, binExpr);
        }
        if (binOp == "==") {
            Type.verifyComparable(LHSType, RHSType, binExpr);
            return Type.BOOLEAN;
        } else if (ORDER_OPERATORS.contains(binOp)) {
            Type.verifyOrdered(LHSType, RHSType, binExpr);
            return Type.BOOLEAN;
        } else if (BOOLEAN_OPERATORS.contains(binOp)) {
            Type.verifyBoolean(LHSType, RHSType, binExpr);
            return Type.BOOLEAN;
        } else if (ARITHMETIC_OPERATORS.contains(binOp)) {
            Type.verifyArithmetic(LHSType, RHSType, binExpr);
            return LHSType;
        } else {
            throw new SemanticAnalysisException("This binary operator " + "'" + binOp + "'" +
                    "doesn't belong to any class of binary operators. " + 
                    "You sure you didn't miss anything??? Bro, don't play with me!!!", binExpr);
        }
    }

    public static final Type verifyUnaryOperation(Type type, UnaryOpExpression unExpr)
        throws SemanticAnalysisException
    {
        String unOp = unExpr.getOperator();
        if (!UNARY_OPERATORS.contains(unOp)) {
            throw new SemanticAnalysisException("Unknown unary operator: " + unOp, unExpr);
        }
        if (BOOLEAN_OPERATORS.contains(unOp)) {
            Type.verifyBoolean(type, unExpr);
            return Type.BOOLEAN;
        } else if (ARITHMETIC_OPERATORS.contains(unOp)) {
            Type.verifyArithmetic(type, unExpr);
            return type;
        } else {
            throw new SemanticAnalysisException("This unary operator " + "'" + unOp + "'" +
                    "doesn't belong to any class of unary operators. " + 
                    "You sure you didn't miss anything??? Bro, don't play with me!!!", unExpr);
        }
    }
}
