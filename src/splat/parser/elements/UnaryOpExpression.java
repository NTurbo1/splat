package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.lang.Operations;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.Value;
import splat.executor.ExecutionException;

public class UnaryOpExpression extends Expression {
    private Expression rightExpr;
    private String operator;

    public UnaryOpExpression(Token tok, Expression rightExpr, String unaryOp) {
        super(tok);
        this.rightExpr = rightExpr;
        this.operator = unaryOp;
    }
    
    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) 
        throws SemanticAnalysisException
    {
        Type type = this.rightExpr.analyzeAndGetType(funcMap, varAndParamMap);
        return Operations.verifyUnaryOperation(type, this);
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ExecutionException
    {
        // FIXME: IMPLEMENT!
        return null;
    }

    public Expression getRightExpr() {
        return this.rightExpr;
    }

    public String getOperator() {
        return this.operator;
    }
}
