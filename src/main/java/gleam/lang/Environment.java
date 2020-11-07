/*
 * Copyright (c) 2001 Guglielmo Nigri.  All Rights Reserved.
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

/**
 * Constituent part of Scheme environment
 *
 */
public class Environment extends AbstractEntity
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public static enum Kind {
        NULL_ENV,
        REPORT_ENV,
        INTERACTION_ENV;
    }

    /** Parent environment */
    Environment parent;

    /** Association function: symbol --> location */
    private final Map<Symbol, Location> assoc = new HashMap<>();

    /** Constructor */
    Environment() {}

    public Environment(Environment parent)
    {
        this.parent = parent;
    }

    /**
     * Associates a symbol in this environment with a value.
     */
    public synchronized void define(Symbol s, Entity v)
    {
        Location loc;
        if ((loc = assoc.get(s)) != null) {
            loc.set(v);
        }
        else{
            assoc.put(s, new Location(v));
        }
    }

    /**
     * Gives the Location for the specified variable.
     *
     * @param s Symbol a variable name
     * @return Location
     * @throws UnboundVariableException if the variable is unbound
     * @see Location
     */
    public Location getLocation(Symbol s)
        throws UnboundVariableException
    {
        Location location = getLocationOrNull(s);
        if (location == null) {
            throw new UnboundVariableException(s);
        }

        return location;
    }

    /**
     * Gives the Location for the specified variable, or null
     * if unbound.
     *
     * @param s Symbol a variable name
     * @return Location or null
     * @see Location
     */
    Location getLocationOrNull(Symbol s)
    {
        Location loc;
        Environment e = this;
        while (e != null) {
            loc = e.assoc.get(s);
            if (loc == null) {
                e = e.parent;
            }
            else {
                return loc;
            }
        }

        // so it is unbound...
        return null;
    }

    /**
     * Looks up a Symbol in the environment
     * by searching this environment and all enclosing
     * environments, up to the topmost (global) environment.
     *
     * @param s Symbol
     * @return Entity
     */
    public Entity lookup(Symbol s)
        throws GleamException
    {
        return getLocation(s).get();
    }

    /** Writes this environment */
    @Override
    public void write(PrintWriter out)
    {
        out.write("#<environment>");
    }
    // DEBUG

    public void dump()
            throws GleamException
    {
        OutputPort out = Interpreter.getInterpreter().getCout();
        out.print("--------------- "+this.toString());
        out.newline();
        for (Symbol s : assoc.keySet()) {
            Location l = assoc.get(s);

            out.write(s);
            out.print("\t" + s.toString());
            out.print("\t" + l.get().toString());
            out.newline();
        }
        if (this.parent != null) {
            parent.dump();
        }
    }
}
