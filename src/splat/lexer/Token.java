package splat.lexer;

public class Token {
    private String value;
    private int line;
    private int column;

    public Token(String value, int line, int column)
    {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String getValue()
    {
        return this.value;
    }

    public int getLine()
    {
        return this.line;
    }

    public int getColumn()
    {
        return this.column;
    }

    @Override
    public String toString()
    {
        return "{value: " + this.value + 
            ", line: " + this.line + 
            ", column: " + this.column + 
            "}";
    }
}
