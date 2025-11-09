package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

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

    public Expression getExpr() {
        return this.expr;
    }
}
