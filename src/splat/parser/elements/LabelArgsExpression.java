package splat.parser.elements;

import java.util.List;
import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.Value;
import splat.executor.ExecutionException;

/**
 * A function call expression ::= <label> ( <args> )
 */
public class LabelArgsExpression extends Expression {
    private String label;
    private List<Expression> args;

    public LabelArgsExpression(Token tok, String label, List<Expression> args) {
        super(tok);
        this.label = label;
        this.args = args;
    }
    
    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) 
        throws SemanticAnalysisException
    {
        FunctionDecl funcDecl = funcMap.get(this.label);
        if (funcDecl == null) {
            throw new SemanticAnalysisException(
                    "No function named '" + this.label + "' is defined!", this);
        }

        Type returnType = funcDecl.getReturnType();
        List<FuncParamDecl> funcParams = funcDecl.getParams();
        int numFuncParams = funcParams.size();

        if (this.args.size() != numFuncParams) {
            throw new SemanticAnalysisException(
                    "The number of arguments doesn't match the number of function parameters for " +
                    "function " + funcDecl, this);
        }

        // Analyze the function args and check if their types match the corresponding 
        // function parameter types.
        for (int i = 0; i < numFuncParams; i++) {
            Expression arg = this.args.get(i);
            Type argType = arg.analyzeAndGetType(funcMap, varAndParamMap);
            Type paramType = funcParams.get(i).getType();
            if (paramType != argType) {
                throw new SemanticAnalysisException(
                        "Expected a function argument of type '" + paramType + "' but got '" + 
                        argType + "'", arg
                    );
            };
        }

        return returnType;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ExecutionException
    {
        // FIXME: IMPLEMENT!
        return null;
    }

    public List<Expression> getArgs() {
        return this.args;
    }

    public String getLabel() {
        return this.label;
    }
}
