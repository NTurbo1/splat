package splat.executor;

import splat.parser.elements.Type;

public class BoolValue extends Value
{
    private boolean value;

    public BoolValue(boolean value)
    {
        super(Type.BOOLEAN);
        this.value = value;
    }

    public boolean getValue() { return this.value; }
}
