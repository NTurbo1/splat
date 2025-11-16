package splat.executor;

import splat.parser.elements.Type;

public class StringValue extends Value
{
    private String value;

    public StringValue(String value)
    {
        super(Type.STRING);
        this.value = value;
    }

    public String getValue() { return this.value; }

    @Override
    public String toString()
    {
        return this.value;
    }
}
