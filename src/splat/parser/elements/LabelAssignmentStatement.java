package splat.parser.elements;

import java.util.Map;
import java.util.Stack;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.ScopeEnvironment;

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
    public void execute(
            Map<String, FunctionDecl> funcMap,
            Map<String, Value> varAndParamMap,
            Stack<ScopeEnvironment> callStack) throws ExecutionException
    {
        this.checkNotFuncLabel(this.label, funcMap);
        Value varVal = this.getVarVal(this.label, callStack, varAndParamMap);
        Value newVarVal = this.expr.evaluate(funcMap, varAndParamMap, callStack);
        this.updateVarVal(this.label, newVarVal, callStack, varAndParamMap);
    }

    private void checkNotFuncLabel(String label, Map<String, FunctionDecl> funcMap)
            throws ExecutionException
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
    }

    public Expression getExpr() {
        return this.expr;
    }

    public String getLabel() {
        return this.label;
    }
}
