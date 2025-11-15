package splat.executor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import splat.parser.elements.FunctionDecl;
import splat.parser.elements.FuncParamDecl;
import splat.parser.elements.VariableDecl;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.Statement;
import splat.parser.elements.Declaration;
import splat.parser.elements.Type;

public class Executor {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap;
	private Map<String, Value> progVarMap;
	
	public Executor(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void runProgram() throws ExecutionException {

		// This sets the maps that will be needed for executing function 
		// calls and storing the values of the program variables
		setMaps();
		
		try {
			
			// Go through and execute each of the statements
			for (Statement stmt : progAST.getStmts()) {
				stmt.execute(funcMap, progVarMap);
			}
			
		// We should never have to catch this exception here, since the
		// main program body cannot have returns
		} catch (ReturnFromCall ex) {
			System.out.println("Internal error!!! The main program body "
					+ "cannot have a return statement -- this should have "
					+ "been caught during semantic analysis!");
			
			throw new ExecutionException("Internal error -- fix your "
					+ "semantic analyzer!", -1, -1);
		}
	}
	
	private void setMaps() throws ExecutionException
    {
        this.funcMap = new HashMap<String, FunctionDecl>();
        this.progVarMap = new HashMap<String, Value>();

		for (Declaration decl : progAST.getDecls()) 
        {
			String label = decl.getLabel().toString();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);
				
			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
                Value varVal = returnZeroValueOf(varDecl);
				progVarMap.put(label, varVal);
			}
		}
	}

    public static final Value returnZeroValueOf(VariableDecl varDecl) throws ExecutionException
    {
        Type varType = varDecl.getType();
        switch (varType)
        {
            case BOOLEAN:
                return new BoolValue(false);
            case STRING:
                return new StringValue("");
            case INTEGER:
                return new IntegerValue(0);
            default:
                throw new ExecutionException(
                    "Unknown type detected during execution: " + varType + 
                    "WTF did your semantic analyzer do???", varDecl
                );
        }
    }

    public static final void removeFuncArgsAndLocalVarsFrom(
        Map<String, Value> varAndParamMap, FunctionDecl funcDecl
    ) {
        if (funcDecl == null) { return; }

        List<FuncParamDecl> params = funcDecl.getParams();
        if (params == null) { return; }
        for (FuncParamDecl param : params) 
        {
            varAndParamMap.remove(param.getLabel());
        }

        List<VariableDecl> localVars = funcDecl.getLocalVarDecls();
        if (localVars == null) { return; }
        for (VariableDecl localVar : localVars)
        {
            varAndParamMap.remove(localVar.getLabel());
        }
    }
}
