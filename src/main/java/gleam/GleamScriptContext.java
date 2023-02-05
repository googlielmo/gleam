package gleam;

import gleam.lang.ExecutionContext;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import gleam.lang.OutputPort;

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class GleamScriptContext implements ScriptContext
{
    private static final List<Integer>
            SCOPES = Arrays.asList(ENGINE_SCOPE, GLOBAL_SCOPE);
    private static final Map<Bindings, GleamBindings>
            bindingsCache = new WeakHashMap<>();
    protected ExecutionContext executionContext;
    protected Writer writer;
    protected Writer errorWriter;
    protected Reader reader;
    protected GleamBindings engineScope;
    protected GleamBindings globalScope;

    public GleamScriptContext(Interpreter interpreter)
    {
        // sets stdin/out/err as reader, writer, errorWriter
        this(new ExecutionContext(interpreter,
                                  new InputPort(
                                          new InputStreamReader(System.in)),
                                  new OutputPort(
                                          new PrintWriter(System.out, true),
                                          System.console() != null),
                                  new OutputPort(
                                          new PrintWriter(System.err, true),
                                          System.console() != null)),
             new GleamBindings(null));
    }

    public GleamScriptContext(ExecutionContext executionContext,
                              Bindings bindings)
    {
        this.executionContext = executionContext;
        this.reader = executionContext.getIn().getReader();
        this.writer = executionContext.getOut().getPrintWriter();
        this.errorWriter = executionContext.getErr().getPrintWriter();
        setBindings(GleamScriptContext
                            .getCachedGleamBindings(bindings), ENGINE_SCOPE);
    }

    static GleamBindings getCachedGleamBindings(Bindings bindings)
    {
        if (bindings instanceof GleamBindings) {
            return (GleamBindings) bindings;
        }

        return bindingsCache.computeIfAbsent(bindings,
                                             GleamScriptContext::wrapBindings);
    }

    private static GleamBindings wrapBindings(Bindings b)
    {
        return new GleamBindings(null, b);
    }

    @Override
    public void setBindings(Bindings bindings, int scope)
    {
        Objects.requireNonNull(bindings);
        GleamBindings gleamBindings = getCachedGleamBindings(bindings);
        switch (scope) {
            case GLOBAL_SCOPE:
                globalScope = gleamBindings;
                executionContext.getInterpreter().setGlobalEnv(globalScope);
                break;
            case ENGINE_SCOPE:
                engineScope = gleamBindings;
                executionContext.getInterpreter().setSessionEnv(engineScope);
                break;
            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    @Override
    public Bindings getBindings(int scope)
    {
        if (scope == ENGINE_SCOPE) {
            return engineScope;
        }
        else if (scope == GLOBAL_SCOPE) {
            return globalScope;
        }
        else {
            throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    @Override
    public void setAttribute(String name, Object value, int scope)
    {
        Objects.requireNonNull(name);
        getBindings(scope).put(name, value);
    }

    @Override
    public Object getAttribute(String name, int scope)
    {
        Objects.requireNonNull(name);
        return getBindings(scope).get(name);
    }

    @Override
    public Object removeAttribute(String name, int scope)
    {
        Objects.requireNonNull(name);
        return getBindings(scope).remove(name);
    }

    @Override
    public Object getAttribute(String name)
    {
        Objects.requireNonNull(name);
        if (engineScope.containsKey(name)) {
            return engineScope.get(name);
        }
        else {
            return globalScope.getOrDefault(name, null);
        }

    }

    @Override
    public int getAttributesScope(String name)
    {
        if (engineScope.containsKey(name)) {
            return ENGINE_SCOPE;
        }
        else if (globalScope != null && globalScope.containsKey(name)) {
            return GLOBAL_SCOPE;
        }
        else {
            return -1;
        }
    }

    @Override
    public Writer getWriter()
    {
        return writer;
    }

    @Override
    public void setWriter(Writer writer)
    {
        this.writer = writer instanceof PrintWriter
                      ? writer
                      : new PrintWriter(writer, true);
        executionContext.setOut(new OutputPort(this.writer, false));
    }

    @Override
    public Writer getErrorWriter()
    {
        return errorWriter;
    }

    @Override
    public void setErrorWriter(Writer writer)
    {
        this.errorWriter = writer instanceof PrintWriter
                           ? writer
                           : new PrintWriter(writer, true);
        executionContext.setErr(new OutputPort(this.errorWriter, false));
    }

    @Override
    public Reader getReader()
    {
        return reader;
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

    public ExecutionContext getExecutionContext()
    {
        return executionContext;
    }
}
