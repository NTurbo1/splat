package splat.parser.elements;

import java.util.List;

import splat.lexer.Token;

public class IfElseStatement extends Statement {
    private Expression expr;
    private List<Statement> stmts;
    private List<Statement> elseStmts;

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

    public Expression getBinExpr() {
        return this.expr;
    }

    public List<Statement> getStmts() {
        return this.stmts;
    }

    public List<Statement> getElseStmts() {
        return this.elseStmts;
    }
}
