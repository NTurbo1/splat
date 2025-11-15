package splat.parser.elements;

import java.util.List;
import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;

public class WhileLoopStatement extends Statement {
    private Expression expr;
    private List<Statement> stmts;

    public WhileLoopStatement(Token tok, Expression expr, List<Statement> stmts) {
        super(tok);
        this.expr = expr;
        this.stmts = stmts;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        Type exprType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
        if (exprType != Type.BOOLEAN) {
            throw new SemanticAnalysisException(
                    "While-loop statement expression should return a boolean type value.", this.expr);
        }

        for (Statement stmt : this.stmts) {
            stmt.analyze(funcMap, varAndParamMap);
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

    public List<Statement> getStmts() {
        return this.stmts;
    }
}
