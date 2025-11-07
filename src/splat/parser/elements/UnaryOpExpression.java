package splat.parser.elements;

import splat.lexer.Token;

public class UnaryOpExpression extends Expression {
    private Expression rightExpr;
    private String operator;

    public UnaryOpExpression(Token tok, Expression rightExpr, String unaryOp) {
        super(tok);
        this.rightExpr = rightExpr;
        this.operator = unaryOp;
    }

    public Expression getRightExpr() {
        return this.rightExpr;
    }

    public String getOperator() {
        return this.operator;
    }
}
