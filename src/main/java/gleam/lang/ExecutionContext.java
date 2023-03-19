/*
 * Copyright (c) 2023 Guglielmo Nigri.  All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it would be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Further, this software is distributed without any warranty that it is
 * free of the rightful claim of any third person regarding infringement
 * or the like.  Any license provided herein, whether implied or
 * otherwise, applies only to this software file.  Patent licenses, if
 * any, provided herein do not apply to combinations of this program with
 * other software, or any other product whatsoever.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write the Free Software Foundation, Inc., 59
 * Temple Place - Suite 330, Boston MA 02111-1307, USA.
 *
 * Contact information: Guglielmo Nigri <guglielmonigri@yahoo.it>
 *
 */

package gleam.lang;

/**
 * The execution context. It holds I/O ports and other context.
 * <p>
 * A default context is accessible in all environments, but can be overridden.
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
    private boolean noisy;

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
        noisy = false;
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

    public boolean isNoisy()
    {
        return noisy;
    }

    public void setNoisy(boolean noisy)
    {
        this.noisy = noisy;
    }
}
