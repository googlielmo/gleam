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

/*
 * Location.java
 *
 * Creation date: 03/11/2001 11.16.18
 */

package gleam.lang;

import java.io.PrintWriter;

/**
 * A location object, which gives compiled (constant-time) read/write access to
 * a variable. Locations are used in compiled Scheme code to avoid interpreted
 * (non constant-time) lookup and set operations on variables.
 */
public final class Location extends AbstractEntity
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Entity value;

    /**
     * Location constructor.
     */
    Location(Entity value)
    {
        this.value = value;
    }

    /** Location evaluates to its content value */
    @Override
    public Entity eval(Environment env, Continuation cont)
    {
        return value;
    }

    /**
     * Sets current referred object
     */
    public void set(Entity obj)
    {
        this.value = obj;
    }

    /**
     * Gets current referred object
     */
    public Entity get()
    {
        return value;
    }

    /**
     * Writes this location
     */
    @Override
    public void write(PrintWriter out)
    {
        out.write("#<location of ");
        value.write(out);
        out.write(">");
    }
}
