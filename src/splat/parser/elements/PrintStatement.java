package splat.parser.elements;

import splat.lexer.Token;

public class PrintStatement extends Statement {
    private Expression expr;

    public PrintStatement(Token tok, Expression expr) {
        super(tok);
        this.expr = expr;
    }

    public Expression getExpr() {
        return this.expr;
    }
}
