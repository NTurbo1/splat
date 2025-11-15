package splat.parser.elements;

import java.util.List;
import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.Executor;

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

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ReturnFromCall, ExecutionException
    {
        FunctionDecl funcDecl = funcMap.get(this.label);
        if (funcDecl == null)
        {
            throw new ExecutionException("Dude, this function " + funcDecl.toString() + 
                " is not even declared! Your semantic analyzer is FUCKED UP!", 
                this
            );
        }

        List<FuncParamDecl> funcParams = funcDecl.getParams();
        if (funcParams != null)
        {
            // Add function arguments to the varAndParamMap so that they're accessible to the statements
            // and expressions in the function body. We'll remove them later before returning from a 
            // function. 
            for (int i = 0; i < funcParams.size(); i++)
            {
                Value argVal = this.args.get(i).evaluate(funcMap, varAndParamMap);
                varAndParamMap.put(funcParams.get(i).getLabel(), argVal);
            }
        }

        List<Statement> funcStmts = funcDecl.getStmts();
        if (funcStmts != null)
        {
            for (Statement stmt : funcStmts) {
                stmt.execute(funcMap, varAndParamMap);
            }
        }

        Executor.removeFuncArgsAndLocalVarsFrom(varAndParamMap, funcDecl);
    }

    public List<Expression> getArgs() {
        return this.args;
    }

    public String getLabel() {
        return this.label;
    }
}
