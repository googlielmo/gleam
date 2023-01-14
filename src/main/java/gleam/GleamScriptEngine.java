package gleam;

import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.JavaObject;

import javax.script.*;
import java.io.Reader;

public class GleamScriptEngine extends AbstractScriptEngine {

    private final Interpreter interpreter;

    public GleamScriptEngine() {
        try {
            this.interpreter = Interpreter.newInterpreter();
        } catch (GleamException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object unwrap(Object value) {
        if (value instanceof JavaObject) {
            return ((JavaObject) value).getObjectValue();
        }
        return value;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        try {
            Object value = interpreter.eval(script);
            return unwrap(value);
        } catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try {
            Object value = interpreter.eval(reader);
            return unwrap(value);
        } catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return new GleamScriptEngineFactory();
    }
}

