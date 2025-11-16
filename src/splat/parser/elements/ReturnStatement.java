package splat.parser.elements;

import java.util.Map;
import java.util.Stack;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.ScopeEnvironment;

public class ReturnStatement extends Statement {
    private Expression expr;

    public ReturnStatement(Token tok, Expression expr) {
        super(tok);
        this.expr = expr;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        FunctionDecl funcDecl = funcMap.get(this.getFuncLabel());
        if (funcDecl == null) { // Assumed the return statement is in the program body not in a funciton.
            // TODO: Wait, can a splat program return something? If so, what specifically? 
            // Maybe int and void?
            //
            // I assume that the program must not return anything for now.

            throw new SemanticAnalysisException("You can't return outside a function!", this);
        } else {
            Type expectedReturnType = funcDecl.getReturnType();

            if (this.expr != null) {
                if (expectedReturnType == Type.VOID) {
                    throw new SemanticAnalysisException(
                            "You can't return an expression from a function that has a return type " +
                            "of 'void'",
                            this.expr
                        );
                }

                Type returnType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);
                if (returnType != expectedReturnType) {
                    throw new SemanticAnalysisException(
                            "Expected a return type '" + expectedReturnType + "', not '" + returnType + 
                            "'",
                            this.expr
                        );
                }
            } else {
                if (expectedReturnType != Type.VOID) {
                    throw new SemanticAnalysisException(
                            "Expected a value/expression of type '" + expectedReturnType + "' to be " +
                            "returned from a function",
                            this
                        );
                }
            }
        }
    }

    @Override
    public void execute(
            Map<String, FunctionDecl> funcMap,
            Map<String, Value> varAndParamMap,
            Stack<ScopeEnvironment> callStack) throws ReturnFromCall, ExecutionException
    {
        FunctionDecl funcDecl = this.getFuncDecl(funcMap);

        Type funcReturnType = funcDecl.getReturnType();
        if (funcReturnType == null)
        {
            throw new ExecutionException(
                "A function must have a return type!!! Go fix your semantic analyzer, kid!",
                this
            );
        }

        Value returnVal = null;
        Type returnExprType = Type.VOID;
        if (this.expr != null) {
            returnVal = this.expr.evaluate(funcMap, varAndParamMap, callStack);
            returnExprType = returnVal.getType();
        }

        if (funcReturnType != returnExprType)
        {
            throw new ExecutionException(
                "The return value type '" + returnExprType + 
                "' doesn't match the function return type '" + funcReturnType + "'",
                this
            );
        }

        throw new ReturnFromCall(returnVal);
    }

    private FunctionDecl getFuncDecl(Map<String, FunctionDecl> funcMap) throws ExecutionException
    {
        FunctionDecl funcDecl = funcMap.get(this.getFuncLabel());

        if (funcDecl == null) 
        {
            throw new ExecutionException(
                "Outside function return statement is detected! Or your semantic analyzer is FUCKED UP!", 
                this
            );
        }

        return funcDecl;
    }

    public Expression getExpr() {
        return this.expr;
    }
}
