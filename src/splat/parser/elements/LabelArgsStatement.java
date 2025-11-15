package splat.parser.elements;

import java.util.List;
import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;

public class LabelArgsStatement extends Statement {
    private String label;
    private List<Expression> args;

    public LabelArgsStatement(Token tok, String label, List<Expression> args) {
        super(tok);
        this.label = label;
        this.args = args;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        FunctionDecl funcDecl = funcMap.get(this.label);
        if (funcDecl == null) {
            throw new SemanticAnalysisException(
                    "No function is defined by the name '" + this.label + "'", this);
        }

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
    }

    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ReturnFromCall, ExecutionException
    {
        // FIXME: IMPLEMENT!
    }

    public List<Expression> getArgs() {
        return this.args;
    }

    public String getLabel() {
        return this.label;
    }
}
