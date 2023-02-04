package gleam.lang;

/**
 * The execution context. It holds I/O ports and other context.
 * <p>
 * A default context is accessible in all environments, but can be overridden.
 * <p>
 *
 * @see SystemEnvironment#SystemEnvironment()
 */
public class ExecutionContext
{
    private final Interpreter intp;
    private InputPort in;
    private OutputPort out;
    private OutputPort err;
    private boolean traceEnabled;

    public ExecutionContext(Interpreter intp,
                            InputPort in,
                            OutputPort out,
                            OutputPort err)
    {
        this.intp = intp;
        this.in = in;
        this.out = out;
        this.err = err;
        traceEnabled = false;
    }

    public Interpreter getInterpreter()
    {
        return intp;
    }

    public InputPort getIn()
    {
        return in;
    }

    public void setIn(InputPort in)
    {
        this.in = in;
    }

    public OutputPort getOut()
    {
        return out;
    }

    public void setOut(OutputPort out)
    {
        this.out = out;
    }

    public OutputPort getErr()
    {
        return err;
    }

    public void setErr(OutputPort err)
    {
        this.err = err;
    }

    public boolean isTraceEnabled()
    {
        return traceEnabled;
    }

    public void setTraceEnabled(boolean traceEnabled)
    {
        this.traceEnabled = traceEnabled;
    }
}
