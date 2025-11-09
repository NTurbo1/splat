package splat.parser.elements;

import splat.lexer.Token;

public class FuncParamDecl extends Declaration {

    private Type type;
	
	public FuncParamDecl(Token tok, String label, Type type) {
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
