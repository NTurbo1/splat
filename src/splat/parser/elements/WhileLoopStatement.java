package splat.parser.elements;

import java.util.List;

import splat.lexer.Token;

public class WhileLoopStatement extends Statement {
    private Expression expr;
    private List<Statement> stmts;

    public WhileLoopStatement(Token tok, Expression expr, List<Statement> stmts) {
        super(tok);
        this.expr = expr;
        this.stmts = stmts;
    }

    public Expression getExpr() {
        return this.expr;
    }

    public List<Statement> getStmts() {
        return this.stmts;
    }
}
