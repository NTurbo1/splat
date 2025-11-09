package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.lang.Operations;
import splat.semanticanalyzer.SemanticAnalysisException;

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
