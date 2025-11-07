package splat.parser.elements;

import splat.lexer.Token;

public class BinaryOpExpression extends Expression {
    private Expression leftExpr;
    private Expression rightExpr;
    private String operator;

    public BinaryOpExpression(Token tok, Expression leftExpr, Expression rightExpr, String binOp) {
        super(tok);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.operator = binOp;
    }

    public Expression getLeftExpr() {
        return this.leftExpr;
    }

    public Expression getRightExpr() {
        return this.rightExpr;
    }

    public String getOperator() {
        return this.operator;
    }
}
