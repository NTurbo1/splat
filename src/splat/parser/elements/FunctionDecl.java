package splat.parser.elements;

import java.util.List;

import splat.lexer.Token;

public class FunctionDecl extends Declaration {

    private List<FuncParamDecl> params;
    private String returnType;
    private List<VariableDecl> localVarDecls;
    private List<Statement> statements;
	
	public FunctionDecl(
            Token tok,
            List<FuncParamDecl> params,
            String returnType,
            List<VariableDecl> localVarDecls,
            List<Statement> statements
    ) {
		super(tok);
        this.params = params;
        this.returnType = returnType;
        this.localVarDecls = localVarDecls;
        this.statements = statements;
	}

    public List<FuncParamDecl> getParams() {
        return this.params;
    }

    public List<VariableDecl> getLocalVarDecls() {
        return this.localVarDecls;
    }

    public List<Statement> getStatements() {
        return this.statements;
    }

    public String getReturnType() {
        return this.returnType;
    }
	
	public String toString() {
		return "{params: " + this.params.toString() + ", returnType: " + this.returnType +
            ", localVarDecls: " + this.localVarDecls.toString() + ", statements: " + 
            this.statements.toString();
	}
}
