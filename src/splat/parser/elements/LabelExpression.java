package splat.parser.elements;

import splat.lexer.Token;

public class LabelExpression extends Expression {
    private String value;

    public LabelExpression(Token tok) {
        super(tok);
        this.value = tok.getValue();
    }

    public String getValue() {
        return this.value;
    }
}
