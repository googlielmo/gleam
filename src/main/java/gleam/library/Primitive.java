/*
 * Copyright (c) 2007 Guglielmo Nigri.  All Rights Reserved.
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
 * Primitive.java
 *
 * Created on 5 feb 2007, 19.33
 *
 */

package gleam.library;

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.List;

/**
 * A primitive procedure of the language. Each primitive should extend this
 * class, overriding exactly one of the apply methods to define its behavior.
 * The method to override should be the one corresponding to the maxArgs of the
 * primitive (0..3, or N when more than 3 or VAR_ARGS). Missing arguments will
 * be represented by null values if minArgs is less than maxArgs.
 */
public abstract class Primitive implements java.io.Serializable
{
    /** binding for a language keyword */
    public static final boolean KEYWORD = true;
    /** binding for an identifier */
    public static final boolean IDENTIFIER = false;
    /** constant to signal a variable (unlimited) number of arguments */
    public static final int VAR_ARGS = -1;
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /** definition environment */
    public final Environment.Kind definitionEnv;
    /** true if this primitive defines a syntax keyword, false otherwise */
    public final boolean keyword;

    // documentation fields
    /** minimum no. of arguments */
    public final int minArgs;
    /** maximum no. of arguments, or VAR_ARGS for a variable number */
    public final int maxArgs;

    // constant values for keyword field
    /** a short note about this primitive */
    public final transient String comment;
    /** an optional longer documentation note */
    public final transient String documentation;

    // constant values for maxArgs field
    /** primitive name, as used in programs, e.g. "car" */
    private final String name;

    Primitive(String name, Environment.Kind definitionEnv, boolean keyword, int minArgs, int maxArgs, String comment, String documentation)
    {
        this.name = name;
        this.definitionEnv = definitionEnv;
        this.keyword = keyword;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        if (comment != null) {
            this.comment = comment;
        } else {
            this.comment = "No documentation defined";
        }

        if (documentation != null) {
            this.documentation = this.comment + "\n" + documentation;
        } else {
            this.documentation = this.comment;
        }

    }

    /**
     * Apply this primitive to zero arguments.
     *
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been
     * modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the
     *                                   execution of this primitive
     */
    @SuppressWarnings("unused")
    public Entity apply0(Environment env, Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply0 not implemented", null);
    }

    /**
     * Apply this primitive to at most one argument.
     *
     * @param arg1 the only argument to this primitive, or null if not present
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been
     * modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the
     *                                   execution of this primitive
     */
    public Entity apply1(Entity arg1, Environment env, Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply1 not implemented", null);
    }

    /**
     * Apply this primitive to at most two arguments.
     *
     * @param arg1 the first argument to this primitive, or null if not present
     * @param arg2 the second argument to this primitive, or null if not
     *             present
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been
     * modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the
     *                                   execution of this primitive
     */
    public Entity apply2(Entity arg1, Entity arg2, Environment env, Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply2 not implemented", null);
    }

    /**
     * Apply this primitive to at most three arguments.
     *
     * @param arg1 the first argument to this primitive, or null if not present
     * @param arg2 the second argument to this primitive, or null if not
     *             present
     * @param arg3 the third argument to this primitive, or null if not present
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been
     * modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the
     *                                   execution of this primitive
     */
    public Entity apply3(Entity arg1, Entity arg2, Entity arg3, Environment env, Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply3 not implemented", null);
    }

    /**
     * Apply this primitive to a variable number of arguments (but more than
     * three).
     *
     * @param args a Scheme list (a Pair) holding the arguments
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been
     * modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the
     *                                   execution of this primitive
     */
    public Entity applyN(List args, Environment env, Continuation cont) throws GleamException
    {
        throw new GleamException(this, "applyN not implemented", null);
    }

    /**
     * Gets the name of this Primitive.
     *
     * @return a String holding the name of this Primitive.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets a representation of this Primitive
     *
     * @return a String representing this Primitive in human readable form
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(":");
        sb.append(minArgs);
        if (minArgs != maxArgs) {
            if (maxArgs < 0) {
                sb.append("..*");
            } else {
                sb.append("..");
                sb.append(maxArgs);
            }
        }
        return sb.toString();
    }
}
