package splat.parser.elements;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.Executor;
import splat.executor.ScopeEnvironment;

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
    public void execute(
            Map<String, FunctionDecl> funcMap,
            Map<String, Value> varAndParamMap,
            Stack<ScopeEnvironment> callStack) throws ReturnFromCall, ExecutionException
    {
        FunctionDecl funcDecl = this.getFunctionDecl(funcMap);
        ScopeEnvironment scopeEnv = this.createFuncScopeEnv();
        this.addLocalVarsToScopeEnv(funcDecl, scopeEnv);
        this.addFuncArgsToScopeEnv(funcDecl, funcMap, varAndParamMap, callStack, scopeEnv);
        callStack.push(scopeEnv);

        List<Statement> funcStmts = funcDecl.getStmts();
        if (funcStmts != null)
        {
            for (Statement stmt : funcStmts) {
                try {
                    stmt.execute(funcMap, varAndParamMap, callStack);
                } catch (ReturnFromCall rfc) {
                    if (rfc.getReturnVal() != null) // non void return statement
                    {
                        throw new ExecutionException(
                            "Returning a value from a function with return type of 'void'!" + 
                            "Probably your semantic analyzer missed it. Ehh... Go fix it!",
                            stmt
                        );
                    }

                    callStack.pop();
                    return;
                }
            }
        }

        callStack.pop();
    }

    public FunctionDecl getFunctionDecl(Map<String, FunctionDecl> funcMap) throws ExecutionException
    {
        FunctionDecl funcDecl = funcMap.get(this.label);
        if (funcDecl == null)
        {
            throw new ExecutionException("Dude, this function " + funcDecl.toString() + 
                " is not even declared! Your semantic analyzer is FUCKED UP!", 
                this
            );
        }

        Type funcReturnType = funcDecl.getReturnType();
        if (funcReturnType != Type.VOID)
        {
            throw new ExecutionException(
                "Functions of function call statements must have a declared return type of 'void'!", 
                this
            );
        }

        return funcDecl;
    }

    private ScopeEnvironment createFuncScopeEnv()
    {
        Map<String, Value> localVarAndParamMap = new HashMap<>();
        ScopeEnvironment scopeEnv = new ScopeEnvironment(localVarAndParamMap);

        return scopeEnv;
    }

    private void addLocalVarsToScopeEnv(
        FunctionDecl funcDecl, ScopeEnvironment scopeEnv
    ) throws ExecutionException 
    {
        List<VariableDecl> localVarDecls = funcDecl.getLocalVarDecls();

        if (localVarDecls != null)
        {
            for (VariableDecl varDecl : localVarDecls)
            {
                Value varVal = Executor.returnZeroValueOf(varDecl);
                scopeEnv
                    .getLocalVarAndParamMap()
                    .put(varDecl.getLabel(), varVal);
            }
        }
    }

    private void addFuncArgsToScopeEnv(
        FunctionDecl funcDecl, Map<String, 
        FunctionDecl> funcMap, 
        Map<String, Value> varAndParamMap,
        Stack<ScopeEnvironment> callStack,
        ScopeEnvironment scopeEnv) throws ExecutionException
    {
        List<FuncParamDecl> funcParams = funcDecl.getParams();

        if (funcParams != null)
        {
            for (int i = 0; i < funcParams.size(); i++)
            {
                Value argVal = this.args.get(i).evaluate(funcMap, varAndParamMap, callStack);
                scopeEnv
                    .getLocalVarAndParamMap()
                    .put(funcParams.get(i).getLabel(), argVal);
            }
        }
    }

    public List<Expression> getArgs() {
        return this.args;
    }

    public String getLabel() {
        return this.label;
    }
}
