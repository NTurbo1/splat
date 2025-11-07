package splat.parser.elements;

import splat.lexer.Token;

public class ReturnStatement extends Statement {
    private Expression expr;

    public ReturnStatement(Token tok, Expression expr) {
        super(tok);
        this.expr = expr;
    }

    public Expression getExpr() {
        return this.expr;
    }
}
