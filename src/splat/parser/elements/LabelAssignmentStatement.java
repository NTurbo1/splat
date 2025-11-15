package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;

public class LabelAssignmentStatement extends Statement {
    private String label;
    private Expression expr;

    public LabelAssignmentStatement(Token tok, String label, Expression expr) {
        super(tok);
        this.label = label;
        this.expr = expr;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        Type LHSType = varAndParamMap.get(this.label);
        if (LHSType == null) {
            throw new SemanticAnalysisException(
                    "Can't assign to a variable '" + this.label + "' that is not declared!", this);
        }

        Type RHSType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
        if (LHSType != RHSType) {
            throw new SemanticAnalysisException(
                    "Can't assign '" + RHSType + "' type to '" + LHSType + "' type!", this.expr);
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ReturnFromCall, ExecutionException
    {
        // FIXME: IMPLEMENT!
    }

    public Expression getExpr() {
        return this.expr;
    }

    public String getLabel() {
        return this.label;
    }
}
