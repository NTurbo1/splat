package splat.parser.elements;

import java.util.Map;
import java.util.Stack;

import splat.lexer.Token;
import splat.lang.Operations;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.Value;
import splat.executor.BoolValue;
import splat.executor.StringValue;
import splat.executor.IntegerValue;
import splat.executor.ExecutionException;
import splat.executor.ScopeEnvironment;

public class BinaryOpExpression extends Expression {
    private Expression leftExpr;
    private Expression rightExpr;
    private String operator;

    public BinaryOpExpression(Token tok, Expression leftExpr, Expression rightExpr, String binOp) {
        super(tok);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.operator = binOp;
    }
    
    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) 
         throws SemanticAnalysisException
    {
        Type LHSType = this.leftExpr.analyzeAndGetType(funcMap, varAndParamMap);
        Type RHSType = this.rightExpr.analyzeAndGetType(funcMap, varAndParamMap);

        return Operations.verifyBinaryOperation(LHSType, RHSType, this);
    }

    @Override
    public Value evaluate(
        Map<String, FunctionDecl> funcMap,
        Map<String, Value> varAndParamMap,
        Stack<ScopeEnvironment> callStack) throws ExecutionException
    {
        Value leftVal = this.leftExpr.evaluate(funcMap, varAndParamMap, callStack);
        Value rightVal = this.rightExpr.evaluate(funcMap, varAndParamMap, callStack);

        if (Operations.ARITHMETIC_OPERATORS.contains(this.operator))
        {
            IntegerValue leftIntVal = (IntegerValue) leftVal;
            IntegerValue rightIntVal = (IntegerValue) rightVal;
            int result = evaluateArithmeticOp(leftIntVal.getValue(), rightIntVal.getValue());

            return new IntegerValue(result);
        }
        if (Operations.BOOLEAN_OPERATORS.contains(this.operator))
        {
            BoolValue leftBoolVal = (BoolValue) leftVal;
            BoolValue rightBoolVal = (BoolValue) rightVal;
            boolean result = evaluateBoolOp(leftBoolVal.getValue(), rightBoolVal.getValue());

            return new BoolValue(result);
        }
        if (Operations.ORDER_OPERATORS.contains(this.operator))
        {
            IntegerValue leftIntVal = (IntegerValue) leftVal;
            IntegerValue rightIntVal = (IntegerValue) rightVal;
            boolean result = evaluateOrderOp(leftIntVal.getValue(), rightIntVal.getValue());

            return new BoolValue(result);
        }
        if (this.operator.equals("=="))
        {
            boolean result = evaluateComparisonOp(leftVal, rightVal);

            return new BoolValue(result);
        }

        throw new ExecutionException("Unknown binary operator: " + this.operator, this);
    }

    private int evaluateArithmeticOp(int left, int right) throws ExecutionException
    {
        switch (this.operator) 
        {
            case "+":
                return left + right;
            case "-":
                return left - right;
            case "*":
                return left * right;
            case "/":
                if (right == 0) {
                    throw new ExecutionException("Can't divide by zero", this);
                }
                return left / right;
            case "%":
                return left % right;
            default:
                throw new ExecutionException("Unknown arithmetic operator: " + this.operator, this);
        }
    }

    private boolean evaluateBoolOp(boolean left, boolean right) throws ExecutionException
    {
        switch (this.operator) 
        {
            case "and":
                return left & right;
            case "or":
                return left | right;
            default:
                throw new ExecutionException("Unknown binary boolean operator: " + this.operator, this);
        }
    }

    private boolean evaluateOrderOp(int left, int right) throws ExecutionException
    {
        switch (this.operator) 
        {
            case "<":
                return left < right;
            case ">":
                return left > right;
            case "<=":
                return left <= right;
            case ">=":
                return left >= right;
            default:
                throw new ExecutionException("Unknown order operator: " + this.operator, this);
        }
    }

    private boolean evaluateComparisonOp(Value left, Value right) throws ExecutionException
    {
        if (left instanceof StringValue)
        {
            String leftStr = ((StringValue) left).getValue();
            String rightStr = ((StringValue) right).getValue();

            return leftStr.equals(rightStr);
        }
        else if (left instanceof IntegerValue)
        {
            int leftInt = ((IntegerValue) left).getValue();
            int rightInt = ((IntegerValue) right).getValue();

            return leftInt == rightInt;
        }
        else if (left instanceof BoolValue)
        {
            boolean leftBool = ((BoolValue) left).getValue();
            boolean rightBool = ((BoolValue) right).getValue();

            return leftBool == rightBool;
        }
        else
        {
            throw new ExecutionException(
                "Left expression of the comparison operation is evaluated to unknown type: " +
                left.getType().toString(),
                this
            );
        }
    }

    public Expression getLeftExpr() {
        return this.leftExpr;
    }

    public Expression getRightExpr() {
        return this.rightExpr;
    }

    public String getOperator() {
        return this.operator;
    }
}
