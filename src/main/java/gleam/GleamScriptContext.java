package gleam;

import gleam.lang.Environment;
import gleam.lang.ExecutionContext;
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

public class GleamScriptContext extends SimpleScriptContext
{
    public static final List<Integer> SCOPES = Arrays.asList(ENGINE_SCOPE, GLOBAL_SCOPE);

    private final ExecutionContext executionContext;

    public GleamScriptContext(Interpreter interpreter)
    {
        super(); // sets stdin/out/err as reader, writer, errorWriter
        boolean isConsole = System.console() != null;
        executionContext = new ExecutionContext(
                interpreter,
                new InputPort(reader),
                new OutputPort(writer, isConsole),
                new OutputPort(errorWriter, isConsole)
        );


        GleamBindings sessionEnv = new GleamBindings(Interpreter.getSchemeReportEnv());
        engineScope = sessionEnv;
        interpreter.setSessionEnv(sessionEnv);

        this.globalScope = null;
        Interpreter.setGlobalEnv(interpreter, new Environment(executionContext));
    }

    @Override
    public void setBindings(Bindings bindings, int scope)
    {
        GleamBindings gleamBindings = getGleamBindings(bindings);
        super.setBindings(bindings, scope);
        switch (scope) {
            case GLOBAL_SCOPE:
                globalScope = gleamBindings;
                if (gleamBindings != null) {
                    Interpreter.setGlobalEnv(executionContext.getInterpreter(), gleamBindings);
                } else {
                    Interpreter.setGlobalEnv(executionContext.getInterpreter(), new Environment(executionContext));
                }
                break;
            case ENGINE_SCOPE:
                Objects.requireNonNull(gleamBindings);
                engineScope = gleamBindings;
                executionContext.getInterpreter().setSessionEnv(gleamBindings);
                break;
            default:
                throw new IllegalArgumentException("unknown scope");
        }
    }

    @Override
    public void setAttribute(String name, Object value, int scope)
    {
        super.setAttribute(name, value, scope);
    }

    @Override
    public void setWriter(Writer writer) {
        this.writer = writer instanceof PrintWriter ?
            writer :
            new PrintWriter(writer, true);
        executionContext.setOut(new OutputPort(this.writer, false));
    }

    @Override
    public void setErrorWriter(Writer writer) {
        this.errorWriter = writer instanceof PrintWriter ?
            writer :
            new PrintWriter(writer, true);
        executionContext.setErr(new OutputPort(this.errorWriter, false));
    }

    @Override
    public void setReader(Reader reader)
    {
        this.reader = reader;
        InputPort inputPort = new InputPort(this.reader);
        executionContext.setIn(inputPort);
    }

    @Override
    public List<Integer> getScopes()
    {
        return SCOPES;
    }

    private GleamBindings getGleamBindings(Bindings bindings)
    {
        if (bindings == null) {
            return null;
        } else if (bindings instanceof GleamBindings) {
            return (GleamBindings) bindings;
        }
        return new GleamBindings(Interpreter.getSchemeReportEnv(), bindings);
    }
}
