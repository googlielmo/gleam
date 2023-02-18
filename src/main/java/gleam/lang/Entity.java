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
import java.io.StringWriter;

/**
 * The interface that all Gleam objects implement.
 * <p>
 * In Scheme, entities are also referred to as "objects", or "values".
 */
public interface Entity extends java.io.Serializable
{

    /**
     * Evaluates this entity in the given environment, with the given
     * continuation.
     *
     * @param env  Environment
     * @param cont Continuation
     *
     * @return Entity a result
     *
     * @throws GleamException in case of error
     */
    default Entity eval(Environment env, Continuation cont) throws GleamException
    {
        // default: evaluating an entity yields the same entity
        return this;
    }

    /**
     * Performs syntax analysis on this entity.
     *
     * @param env Environment
     *
     * @return Entity
     *
     * @throws GleamException in case of syntax error
     */
    default Entity analyze(Environment env) throws GleamException
    {
        // default: analyzing an entity yields the same entity
        return this;
    }

    /**
     * Performs environment optimization on this entity.
     *
     * @param env Environment
     *
     * @return Entity
     *
     * @throws GleamException in case of error
     */
    default Entity optimize(Environment env) throws GleamException
    {
        // default: optimizing an entity yields the same entity
        return this;
    }

    /**
     * Writes this entity in machine-readable form.
     *
     * @param out PrintWriter
     */
    PrintWriter write(PrintWriter out);

    /**
     * Writes this entity in human-readable form.
     *
     * @param out PrintWriter
     */
    default PrintWriter display(PrintWriter out)
    {
        // default: use write method
        return write(out);
    }

    /**
     * Returns a representation of this entity in the same format as a call to
     * <code>write<code/> would produce.
     *
     * @return a string representation of the entity.
     */
    default String toWriteFormat()
    {
        StringWriter sw;
        write(new PrintWriter(sw = new StringWriter()));
        return sw.toString();
    }
}
