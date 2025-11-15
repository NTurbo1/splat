package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.ReturnFromCall;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.executor.StringValue;
import splat.executor.IntegerValue;
import splat.executor.BoolValue;

public class PrintStatement extends Statement {
    private Expression expr;

    public PrintStatement(Token tok, Expression expr) {
        super(tok);
        this.expr = expr;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
        throws SemanticAnalysisException
    {
        // Well, seems like you can print any type to the console...
        //
        // BUT ANALYZE THE EXPRESSION ANYWAY FOR OTHER SEMATIC RULES! We just don't care about the type!
        this.expr.analyzeAndGetType(funcMap, varAndParamMap);
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ReturnFromCall, ExecutionException
    {
        Value val = this.expr.evaluate(funcMap, varAndParamMap);
        Type valType = val.getType();
        if (valType == Type.STRING)
        {
            StringValue strVal = (StringValue) val;
            System.out.print(strVal.getValue());
        }
        else if (valType == Type.INTEGER)
        {
            IntegerValue intVal = (IntegerValue) val;
            System.out.print(intVal.getValue());
        }
        else if (valType == Type.BOOLEAN)
        {
            BoolValue boolVal = (BoolValue) val;
            System.out.print(boolVal.getValue());
        }
        else
        {
            throw new ExecutionException(
                "Uknown type was detected during execution: " + valType.toString() +
                ". Man, go fix your semantic analyzer!",
                this.expr
            );
        }
    }

    public Expression getExpr() {
        return this.expr;
    }
}
