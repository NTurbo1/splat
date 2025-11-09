package splat.parser.elements;

import splat.lexer.Token;

public class VariableDecl extends Declaration {

    private Type type;
	
	public VariableDecl(Token tok, String label, Type type) {
		super(tok, label);
        this.type = type;
	}

    public Type getType() {
        return this.type;
    }
	
	public String toString() {
        return this.getLabel() + ": " + this.type;
	}
}
