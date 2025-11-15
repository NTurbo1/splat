package splat.executor;

import splat.parser.elements.Type;

public class IntegerValue extends Value
{
    private int value;

    public IntegerValue(int value)
    {
        super(Type.INTEGER);
        this.value = value;
    }

    public int getValue() { return this.value; }
}
