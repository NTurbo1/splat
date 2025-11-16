package splat.parser.elements;

import java.util.Map;
import java.util.Stack;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.executor.ScopeEnvironment;

public abstract class Statement extends ASTElement {

    /*
     * Label of the function of which the statement is in. If the funcLabel is null or empty, then
     * the statement is assumed to be inside of the program body and outside of any function.
     */
    private String funcLabel;

	public Statement(Token tok) {
		super(tok);
	}

	/**
	 * This will be needed for Phase 3 - this abstract method will need to be
	 * implemented by every Statement subclass.  This method essentially does
	 * semantic analysis on the statement, and all sub-expressions that might
	 * make up the statement.   funcMap and varAndParamMap are needed for 
	 * performing semantic analysis and type retrieval for the 
	 * sub-expressions.
	 */
    public abstract void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) 
            throws SemanticAnalysisException;
	
	/**
	 * This will be needed for Phase 4 - this abstract method will need to be
	 * implemented by every Statement subclass.  This method is used to 
	 * execute each statement, which may result in output to the console, or
	 * updating the varAndParamMap.  Both of the given maps may be needed for 
	 * evaluating any sub-expressions in the statement.
	 */
    public abstract void execute(
        Map<String, FunctionDecl> funcMap, 
        Map<String, Value> varAndParamMap, 
        Stack<ScopeEnvironment> callStack
    ) throws ReturnFromCall, ExecutionException;

    public Value getVarVal(String label, Stack<ScopeEnvironment> callStack, Map<String, Value> progVarMap)
        throws ExecutionException
    {
        Value varVal = null;

        if (callStack.empty())
        {
            varVal = progVarMap.get(label);
        }
        else
        {
            varVal = callStack.peek().getLocalVarAndParamMap().get(label);

            if (varVal == null)
            {
                varVal = progVarMap.get(label);
            }
        }

        if (varVal == null) // variable with the label doesn't exist in the program
        {
            throw new ExecutionException(
                "WTF, dude??? Nothing found with the label '" + label + 
                "'! Your semantic analyzer is FUCKED UP! GO FIX IT!!!",
                this
            );
        }

        return varVal;
    }

    public void updateVarVal(
        String label, Value newVal, Stack<ScopeEnvironment> callStack, Map<String, Value> progVarMap
    ) throws ExecutionException
    {
        if (callStack.empty())
        {
            if (progVarMap.containsKey(label))
            {
                progVarMap.put(label, newVal);
            }
            else // variable with the label doesn't exist in the program
            {
                throw new ExecutionException(
                    "Naaaaah, bro, this can't be... No variable found with the label '" + label + 
                    "' while trying to update the variable value to a new value... Just no words...",
                    this
                );
            }
        }
        else
        {
            Map<String, Value> localVarAndParamMap = callStack.peek().getLocalVarAndParamMap();

            if (localVarAndParamMap.containsKey(label))
            {
                localVarAndParamMap.put(label, newVal);
            }
            else
            {
                if (progVarMap.containsKey(label))
                {
                    progVarMap.put(label, newVal);
                }
                else // variable with the label doesn't exist in the program
                {
                    throw new ExecutionException(
                        "Naaaaah, bro, this can't be... No variable found with the label '" + label + 
                        "' while trying to update the variable value to a new value... Just no words...",
                        this
                    );
                }
            }
        }
    }

    public String getFuncLabel() {
        return this.funcLabel;
    }

    public void setFuncLabel(String funcLabel) {
        this.funcLabel = funcLabel;
    }
}
