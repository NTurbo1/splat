package splat.parser.elements;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.BoolValue;
import splat.executor.ScopeEnvironment;

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
    public void execute(
            Map<String, FunctionDecl> funcMap,
            Map<String, Value> varAndParamMap,
            Stack<ScopeEnvironment> callStack) throws ReturnFromCall, ExecutionException
    {
        boolean exprEvaluatedToTrue = false;
        do {
            Value exprVal = this.expr.evaluate(funcMap, varAndParamMap, callStack);
            Type exprValType = exprVal.getType();
            if (exprValType != Type.BOOLEAN)
            {
                throw new ExecutionException(
                    "While-loop statement expression must be 'Boolean' but got '" + exprValType + "'.",
                    this.expr
                );
            }

            exprEvaluatedToTrue = ((BoolValue) exprVal).getValue();
            if (exprEvaluatedToTrue)
            {
                if (this.stmts != null)
                {
                    for (Statement stmt : this.stmts)
                    {
                        stmt.execute(funcMap, varAndParamMap, callStack);
                    }
                }
            }
        } while (exprEvaluatedToTrue);
    }

    public Expression getExpr() {
        return this.expr;
    }

    public List<Statement> getStmts() {
        return this.stmts;
    }
}
