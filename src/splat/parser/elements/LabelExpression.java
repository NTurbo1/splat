package splat.parser.elements;

import java.util.Map;

import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

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

    public String getValue() {
        return this.value;
    }
}
