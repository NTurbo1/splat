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
        FunctionDecl funcDecl = funcMap.get(this.label);
        if (funcDecl != null)
        {
            throw new ExecutionException(
                "Sorry, dude, SPLAT doesn't support functional programming! Can't assign to a function " +
                "label '" + this.label + "'. Yeah, I know... it fucking sucks...", 
                this
            );
        }

        Value varVal = varAndParamMap.get(this.label);
        if (varVal == null)
        {
            throw new ExecutionException(
                "WTF, dude??? Nothing found with the label '" + this.label + 
                "'! Your semantic analyzer is FUCKED UP! GO FIX IT!!!",
                this
            );
        }

        Value newVarVal = this.expr.evaluate(funcMap, varAndParamMap);
        varAndParamMap.put(this.label, newVarVal);
    }

    public Expression getExpr() {
        return this.expr;
    }

    public String getLabel() {
        return this.label;
    }
}
