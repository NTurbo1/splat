package splat.executor;

import splat.parser.elements.Type;

public class Value {
    private Type type;

    public Value(Type type) { this.type = type; }

    public Type getType() { return this.type; }
}
