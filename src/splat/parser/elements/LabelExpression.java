package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.Value;
import splat.executor.ExecutionException;

public class LabelExpression extends Expression {
    private String value;

    public LabelExpression(Token tok) {
        super(tok);
        this.value = tok.getValue();
    }
    
    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) 
        throws SemanticAnalysisException
    {
        Type type = varAndParamMap.get(this.value);
        if (type == null) {
            throw new SemanticAnalysisException("Undefined variable: " + this.value, this);
        }

        return type;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap)
        throws ExecutionException
    {
        Value value = varAndParamMap.get(this.value);
        if (value == null) {
            throw new ExecutionException(
                "Undefined variable detected during execution: " + this.value + 
                ". Sigh... go check your semantic analyzer, alright?! Just check it...",
                this
            );
        }

        return value;
    }

    public String getValue() {
        return this.value;
    }
}
