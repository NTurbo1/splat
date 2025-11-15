package splat.parser.elements;

import java.util.Map;
import java.util.List;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.Value;
import splat.executor.BoolValue;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;

public class IfElseStatement extends Statement {
    private Expression expr;
    private List<Statement> stmts;
    private List<Statement> elseStmts;

    private boolean returns;

    public IfElseStatement(
            Token tok, 
            Expression expr, 
            List<Statement> stmts,
            List<Statement> elseStmts
    ) {
        super(tok);
        this.expr = expr;
        this.stmts = stmts;
        this.elseStmts = elseStmts;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        Type type = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
        if (type != Type.BOOLEAN) {
            throw new SemanticAnalysisException(
                    "If-else statement expression should return a boolean type value.", this.expr);
        }

        FunctionDecl funcDecl = funcMap.get(this.getFuncLabel());
        boolean funcMustReturn = funcDecl != null && funcDecl.getReturnType() == Type.VOID ? false : true;
        boolean ifStmtReturns = false;
        boolean elseStmtReturns = false;

        for (Statement stmt : this.stmts) {
            stmt.analyze(funcMap, varAndParamMap);

            if (stmt instanceof ReturnStatement) { ifStmtReturns = true; }
        }
        if (!ifStmtReturns && 
            (this.stmts != null && this.stmts.size() > 0) && 
            (this.stmts.get(this.stmts.size() - 1) instanceof IfElseStatement))
            // TODO: You may want to check if the last statement is a while-loop statement and
            // that it's condition expression is always true.
        {
            IfElseStatement lastIfElseStmt = (IfElseStatement) this.stmts.get(this.stmts.size() - 1);
            ifStmtReturns = lastIfElseStmt.returns();
        }

        if (this.elseStmts != null) {
            for (Statement stmt : this.elseStmts) {
                stmt.analyze(funcMap, varAndParamMap);

                if (stmt instanceof ReturnStatement) { elseStmtReturns = true; }
            }
        }
        if (!elseStmtReturns && 
            (this.elseStmts != null && this.elseStmts.size() > 0) && 
            (this.elseStmts.get(this.elseStmts.size() - 1) instanceof IfElseStatement))
            // TODO: You may want to check if the last statement is a while-loop statement and
            // that it's condition expression is always true.
        {
            IfElseStatement lastIfElseStmt = (IfElseStatement) this.elseStmts
                .get(this.elseStmts.size() - 1);
            elseStmtReturns = lastIfElseStmt.returns();
        }

        // If the function has a non void return type and the if clause returns, then the corresponding
        // else clause must return as well.
        // If the function has a non void return type and the if clause does NOT return, then the
        // corresponding else clause must NOT return as well.
        //
        // We don't care if the function has void return type.
        if (funcMustReturn && (ifStmtReturns != elseStmtReturns)) {
            System.out.println("funcMustReturn: " + funcMustReturn);
            System.out.println("ifStmtReturns: " + ifStmtReturns);
            System.out.println("elseStmtReturns: " + elseStmtReturns);
            throw new SemanticAnalysisException(
                    "Missing a return statement inside the if-else construct.", this);
        }

        this.returns = ifStmtReturns;
    }

    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ReturnFromCall, ExecutionException
    {
        Value exprValue = this.expr.evaluate(funcMap, varAndParamMap);
        Type exprValType = exprValue.getType();
        if (exprValType != Type.BOOLEAN) 
        {
            throw new ExecutionException(
                "If-else statement expression evaluated to a value of type non-boolean: " + 
                exprValType, this.expr
            );
        }

        BoolValue boolVal = (BoolValue) exprValue;
        if (boolVal.getValue()) {
            if (this.stmts == null) {
                return;
            }
            for (Statement stmt : this.stmts)
            {
                stmt.execute(funcMap, varAndParamMap);
            }
        } else {
            if (this.elseStmts == null) {
                return;
            }
            for (Statement stmt : this.elseStmts)
            {
                stmt.execute(funcMap, varAndParamMap);
            }
        }
    }

    public Expression getBinExpr() {
        return this.expr;
    }

    public List<Statement> getStmts() {
        return this.stmts;
    }

    public List<Statement> getElseStmts() {
        return this.elseStmts;
    }

    public boolean returns() {
        return this.returns;
    }
}
