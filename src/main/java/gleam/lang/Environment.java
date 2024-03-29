/*
 * Copyright (c) 2001-2023 Guglielmo Nigri.  All Rights Reserved.
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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Constituent part of Scheme environment.
 */
public class Environment extends AbstractEntity
{

    private static final long serialVersionUID = 1L;

    /**
     * Association function: <code>symbol -> location</code>.
     */
    protected final Map<Symbol, Location> assoc = new HashMap<>();

    /**
     * Parent environment
     */
    protected Environment parent;

    /**
     * The ExecutionContext. If <code>null</code>, this environment is assumed to have the same
     * context of its parent.
     */
    transient private ExecutionContext executionContext;

    public enum Kind
    {
        NULL_ENV, REPORT_ENV, INTERACTION_ENV
    }

    public Environment(Environment parent)
    {
        this(parent, parent == null ? null : parent.executionContext);
    }

    private Environment(Environment parent, ExecutionContext executionContext)
    {
        this.parent = parent;
        this.executionContext = executionContext;
    }

    public Environment(ExecutionContext ctx)
    {
        this(null, ctx);
    }

    public ExecutionContext getExecutionContext()
    {
        Environment env = this;
        ExecutionContext ctx = null;
        while (ctx == null && env != null) {
            ctx = env.executionContext;
            env = env.parent;
        }
        if (ctx == null) {
            // should never happen
            throw new IllegalStateException("internal error: missing ExecutionContext");
        }
        return ctx;
    }

    public void setExecutionContext(ExecutionContext ctx)
    {
        this.executionContext = ctx;
    }

    /**
     * Associates a symbol in this environment with a value.
     */
    public synchronized Location define(Symbol s, Entity v)
    {
        Objects.requireNonNull(v);
        Location loc;
        if ((loc = assoc.get(s)) == null) {
            loc = new Location(v);
            assoc.put(s, loc);
        }
        else {
            loc.set(v);
        }
        return loc;
    }

    /**
     * Looks up a Symbol in the environment by searching this environment and all enclosing
     * environments, up to the topmost (global) environment.
     *
     * @param s Symbol
     *
     * @return Entity
     */
    public Entity lookup(Symbol s) throws GleamException
    {
        return getLocation(s).get();
    }

    /**
     * Gives the Location for the specified variable.
     *
     * @param s Symbol a variable name
     *
     * @return Location
     *
     * @throws UnboundVariableException if the variable is unbound
     * @see Location
     */
    public Location getLocation(Symbol s) throws UnboundVariableException
    {
        Location location = getLocationOrNull(s);
        if (location == null) {
            throw new UnboundVariableException(s);
        }

        return location;
    }

    /**
     * Gives the Location for the specified variable, or null if unbound.
     *
     * @param s Symbol a variable name
     *
     * @return Location or null
     *
     * @see Location
     */
    Location getLocationOrNull(Symbol s)
    {
        Location loc;
        Environment e = this;
        while (e != null) {
            loc = e.assoc.get(s);
            if (loc == null) {
                e = e.getParent();
            }
            else {
                return loc;
            }
        }

        // it's unbound
        return null;
    }

    public Environment getParent()
    {
        return parent;
    }

    public void setParent(Environment parent)
    {
        this.parent = parent;
    }

    /**
     * Writes this environment.
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.write("#<environment>");
        return out;
    }

    /**
     * Dumps the contents of the environments to the current output port for debugging.
     *
     * @throws GleamException in case of errors
     */
    public void dump() throws GleamException
    {
        Environment env = this;
        ExecutionContext ctx = null;
        while (ctx == null && env != null) {
            ctx = env.executionContext;
            env = env.parent;
        }
        if (ctx == null) {
            // should never happen
            throw new IllegalStateException("missing ExecutionContext");
        }
        OutputPort out = ctx.getOut();
        if (out == null) {
            throw new GleamException("OutputPort null in Environment");
        }
        out.printf("/——————————————— Environment %s --\\ \n", this);
        out.printf("|——————————————— ExecutionContext :       %s \n",
                   this.executionContext);
        out.printf("| \n");
        for (Symbol s : assoc.keySet()) {
            Location l = assoc.get(s);
            out.printf("|       %s\t: %s\n", s.toString(), l.get().toString());
        }
        out.printf("\\—————————————————————————————————————————————/\n");
        if (this.getParent() != null) {
            getParent().dump();
        }
    }
}
