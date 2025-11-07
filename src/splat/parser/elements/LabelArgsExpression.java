package splat.parser.elements;

import java.util.List;

import splat.lexer.Token;

public class LabelArgsExpression extends Expression {
    private String label;
    private List<Expression> args;

    public LabelArgsExpression(Token tok, String label, List<Expression> args) {
        super(tok);
        this.label = label;
        this.args = args;
    }

    public List<Expression> getArgs() {
        return this.args;
    }

    public String getLabel() {
        return this.label;
    }
}
