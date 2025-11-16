package splat.parser.elements;

import java.util.Map;
import java.util.Stack;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.ScopeEnvironment;

public abstract class Expression extends ASTElement {

    public Expression(Token tok) {
		super(tok);
	}

	/**
	 * This will be needed for Phase 3 - this abstract method will need to be
	 * implemented by every Expression subclass.  This method does two things:
	 * 
	 * 1) Performs typechecking and semantic analysis on this expression, and
	 * recursively calls the same method on any sub-expressions.  Note that we
	 * will usually need the types of the immediate sub-expressions to make 
	 * sure all the parts of this expression are of proper types.
	 * 
	 * 2) Determines the type of this expression. 
	 * 
	 * funcMap is needed in case this expression or a sub-expression contains
	 * a function call -- we would need to make sure the argument number and 
	 * types match, and also get the return type.
	 * 
	 * varAndParamMap is needed in case this expression or a sub-expression
	 * contains variables or parameters -- we use this map to keep track of
	 * what items are currently in scope, and what their types are
     * */
    public abstract Type analyzeAndGetType(
            Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap
        ) throws SemanticAnalysisException;
	
	/**
	 * This will be needed for Phase 4 - this abstract method will need to be
	 * implemented by every Expression subclass.  This method is used to 
	 * "calculate" the value of this expression, which will usually require we
	 * recursively call the same method on all sub-expressions. 
	 *
	 * funcMap is needed in case this expression or a sub-expression contains
	 * a function call -- we will have to evaluate the individual arguments and 
	 * create a new varAndParamMap to bind the function params to the new values
	 * and then execute the function body.  More on this later...
	 *  
	 * varAndParamMap is needed in case this expression or a sub-expression
	 * contains variables or parameters -- we use this map to keep track of the
	 * values of the items that are currently in scope
	 */
    public abstract Value evaluate(
        Map<String, FunctionDecl> funcMap,
        Map<String, Value> varAndParamMap,
        Stack<ScopeEnvironment> callStack
    ) throws ExecutionException;

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
}
