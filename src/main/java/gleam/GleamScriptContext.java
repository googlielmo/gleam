package gleam;

import gleam.lang.Environment;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import gleam.lang.OutputPort;

import javax.script.Bindings;
import javax.script.SimpleScriptContext;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GleamScriptContext extends SimpleScriptContext {
    public static final List<Integer> SCOPES = Arrays.asList(ENGINE_SCOPE, GLOBAL_SCOPE);

    private final Interpreter interpreter;

    public GleamScriptContext(Interpreter interpreter) {
        super(); // sets stdin/out/err as reader, writer, errorWriter
        this.interpreter = interpreter;

        GleamBindings sessionEnv = new GleamBindings(Interpreter.getSchemeReportEnv());
        this.engineScope = sessionEnv;
        this.interpreter.setSessionEnv(sessionEnv);

        this.globalScope = null;
        boolean isConsole = System.console() != null;
        Interpreter.setGlobalEnv(this.interpreter,
            Environment.newEnvironment(new InputPort(reader),
                new OutputPort(writer, isConsole)));
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        GleamBindings gleamBindings = getGleamBindings(bindings);
        super.setBindings(bindings, scope);
        switch (scope) {
            case GLOBAL_SCOPE:
                this.globalScope = gleamBindings;
                if (gleamBindings != null) {
                    Interpreter.setGlobalEnv(interpreter, gleamBindings);
                } else {
                    Interpreter.setGlobalEnv(interpreter,
                        Environment.newEnvironment(new InputPort(reader),
                            new OutputPort(writer, false)));
                }
                break;
            case ENGINE_SCOPE:
                Objects.requireNonNull(gleamBindings);
                this.engineScope = gleamBindings;
                interpreter.setSessionEnv(gleamBindings);
                break;
            default:
                throw new IllegalArgumentException("unknown scope");
        }
    }

    private GleamBindings getGleamBindings(Bindings bindings) {
        if (bindings == null) {
            return null;
        } else if (bindings instanceof GleamBindings) {
            return (GleamBindings) bindings;
        }
        return new GleamBindings(Interpreter.getSchemeReportEnv(), bindings);
    }


    @Override
    public void setAttribute(String name, Object value, int scope) {
        super.setAttribute(name, value, scope);
    }

    @Override
    public void setWriter(Writer writer) {
        this.writer = writer instanceof PrintWriter ?
            writer :
            new PrintWriter(writer, true);
        interpreter.setCout(new OutputPort(this.writer, false));
    }

    @Override
    public void setErrorWriter(Writer writer) {
        this.errorWriter = writer instanceof PrintWriter ?
            writer :
            new PrintWriter(writer, true);
        // TODO Cerr in interpreter
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
        InputPort inputPort = new InputPort(this.reader);
        interpreter.setCin(inputPort);
        interpreter.getSessionEnv().setIn(inputPort);
    }

    @Override
    public List<Integer> getScopes() {
        return SCOPES;
    }
}
