package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;

public class PrintStatement extends Statement {
    private Expression expr;

    public PrintStatement(Token tok, Expression expr) {
        super(tok);
        this.expr = expr;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        // Well, seems like you can print any type to the console...
        //
        // BUT ANALYZE THE EXPRESSION ANYWAY FOR OTHER SEMATIC RULES! We just don't care about the type!
        this.expr.analyzeAndGetType(funcMap, varAndParamMap);
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
}
