package splat.parser.elements;

import splat.lexer.Token;

public class LabelAssignmentStatement extends Statement {
    private String label;
    private Expression expr;

    public LabelAssignmentStatement(Token tok, String label, Expression expr) {
        super(tok);
        this.label = label;
        this.expr = expr;
    }

    public Expression getExpr() {
        return this.expr;
    }

    public String getLabel() {
        return this.label;
    }
}
