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

import java.util.*;

/**
 * Constituent part of Scheme environment
 *
 */
public class Environment extends Entity
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** Parent environment */
    Environment parent;

    /** Interpreter */
    private transient Interpreter intp;

    /** Association function: symbol --> location */
    private Map assoc;

    /** Constructor */
    public Environment(Environment p)
    {
        parent = p;
        if (p != null) intp = p.intp;
        assoc = new HashMap();
    }

    /**
     * Returns the current interpreter
     * @return the current interpreter
     */
    public Interpreter getInterpreter() {
        //return intp;
        return intp;
    }

    void setInterpreter(Interpreter interpreter) {
        this.intp = interpreter;
    }

    /**
     * Associates a symbol in this environment with a value.
     */
    public synchronized void define(Symbol s, Entity v)
    {
        java.lang.Object loc;
        if ((loc = assoc.get(s)) != null) {
            ((Location) loc).set(v);
        }
        else{
            assoc.put(s, new Location(v));
        }
    }

    /**
     * Gives the Location for the specified variable.
     *
     * @param s Symbol
     * @return Location
     * @see Location
     */
    public Location getLocation(Symbol s)
        throws GleamException
    {
        java.lang.Object o;
        Environment e = this;
        while (e != null) {
            o = e.assoc.get(s);
            if (o == null) {
                e = e.parent;
            }
            else {
                return (Location) o;
            }
        }
        // so it is unbound...
        throw new GleamException("Unbound variable: " + s.value, s);
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
    public void write(java.io.PrintWriter out)
    {
        out.write("#<environment>");
    }

    // DEBUG
    public void dump() {
        OutputPort out = System.getCout();
        out.print("--------------- "+this.toString());
        out.newline();
        for (Iterator iter = assoc.keySet().iterator(); iter.hasNext(); ) {
            Symbol s = (Symbol) iter.next();
            Location l = (Location) assoc.get(s);

            out.write(s);
            out.print("\t"+s.toString());
            out.print("\t"+l.get().toString());
            out.newline();
        }
        if (this.parent != null) {
            parent.dump();
        }
    }
}
