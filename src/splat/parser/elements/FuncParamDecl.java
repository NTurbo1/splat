package splat.parser.elements;

import splat.lexer.Token;

public class FuncParamDecl extends Declaration {

    private String label;
    private String type;
	
	public FuncParamDecl(Token tok, String label, String type) {
		super(tok);
        this.label = label;
        this.type = type;
	}

    public String getLabel() {
        return this.label;
    }

    public String getType() {
        return this.type;
    }
	
	public String toString() {
		return "{label: " + label + ", type: " + type + "}";
	}
}
