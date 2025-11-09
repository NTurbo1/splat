package splat.parser.elements;

import java.util.List;

import splat.lexer.Token;

public class FunctionDecl extends Declaration {

    private List<FuncParamDecl> params;
    private Type returnType;
    private List<VariableDecl> localVarDecls;
    private List<Statement> statements;
	
	public FunctionDecl(
            Token tok,
            String label,
            List<FuncParamDecl> params,
            Type returnType,
            List<VariableDecl> localVarDecls,
            List<Statement> statements
    ) {
		super(tok, label);
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

    public List<Statement> getStmts() {
        return this.statements;
    }

    public Type getReturnType() {
        return this.returnType;
    }
	
    @Override
	public String toString() 
    {
        StringBuilder funcDeclSB = new StringBuilder();
        funcDeclSB.append(this.getLabel());
        funcDeclSB.append("(");

        StringBuilder paramsSB = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                paramsSB.append(", ");
            }
            paramsSB.append(params.get(i).toString());
        }
        String paramsStr = paramsSB.toString();

        funcDeclSB.append(paramsStr);
        funcDeclSB.append("): ");
        funcDeclSB.append(this.returnType.toString());
        funcDeclSB.append(";");

        return funcDeclSB.toString();
	}
}
