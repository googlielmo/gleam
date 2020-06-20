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
import java.io.StringWriter;

/**
 * The base class for all Gleam objects. Entities are also referred to as 
 * "objects" or "values".
 */
public abstract class Entity implements java.io.Serializable
{
    /**
     * Performs syntax analysis on this entity.
     */
    public Entity analyze()
        throws GleamException
    {
        // default: analyzing an entity yields same entity
        return this;
    }

    /**
     * Evaluates this entity in the given environment,
     * with the given continuation.
     */
    public Entity eval(Environment env, Continuation cont)
        throws GleamException
    {
        // default: evaluating an entity yelds same entity
        return this;
    }

    /**
     * Performs environment optimization on this entity.
     */
    public Entity optimize(Environment env)
        throws GleamException
    {
        // default: optimizing an entity yields same entity
        return this;
    }

    /**
     * Writes this entity in machine-readable form
     */
    abstract public void write(java.io.PrintWriter out);

    /**
     * Writes this entity in human-readable form
     */
    public void display(java.io.PrintWriter out)
    {
        // default: use write method
        write(out);
    }

    /**
     * Returns a string representation of this entity, the same as 'display' 
     * produces.
     * 
     * @return a string representation of the entity.
     */
    public String toString() {
        StringWriter sw;
        display(new PrintWriter(sw = new StringWriter()));
        return sw.toString();
    }
}