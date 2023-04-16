/*
 * Copyright (c) 2007-2023 Guglielmo Nigri.  All Rights Reserved.
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
 * Created on February 5, 2007, 19.33
 */

package gleam.library;

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.List;

/**
 * A primitive procedure of the language. Each primitive should extend this class, overriding
 * exactly one of the apply methods to define its behavior. The method to override should be the one
 * corresponding to the maxArgs of the primitive (0..3, or N when more than 3 or VAR_ARGS). Missing
 * arguments will be represented by null values if minArgs is less than maxArgs.
 */
public class Primitive implements Proc0, Proc1, Proc2, Proc3, ProcN,
                                  java.io.Serializable
{
    /** binding for a language keyword */
    public static final boolean KEYWORD = true;

    /** binding for an identifier */
    public static final boolean IDENTIFIER = false;

    /** constant to signal a variable (unlimited) number of arguments */
    public static final int VAR_ARGS = -1;

    private static final long serialVersionUID = 2L;
    /** definition environment */
    public final Environment.Kind definitionEnv;
    /** true if this primitive defines a syntax keyword, false otherwise */
    public final boolean keyword;

    /** minimum no. of arguments */
    public final int minArgs;
    /** maximum no. of arguments, or VAR_ARGS for a variable number */
    public final int maxArgs;

    /** a short note about this primitive */
    public final transient String comment;

    /** an optional longer documentation note */
    public final transient String documentation;

    /** primitive name, as used in programs, e.g. "car" */
    private final String name;

    public final Proc0 proc0;
    public final Proc1 proc1;
    public final Proc2 proc2;
    public final Proc3 proc3;
    public final ProcN procN;

    private Primitive(String name,
                      Environment.Kind definitionEnv,
                      boolean keyword,
                      int minArgs,
                      int maxArgs,
                      String comment,
                      String documentation,
                      Proc0 proc0,
                      Proc1 proc1,
                      Proc2 proc2,
                      Proc3 proc3,
                      ProcN procN)
    {
        this.name = name;
        this.definitionEnv = definitionEnv;
        this.keyword = keyword;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        if (comment != null) {
            this.comment = comment;
        }
        else {
            this.comment = "No documentation defined";
        }

        if (documentation != null) {
            this.documentation = this.comment + "\n" + documentation;
        }
        else {
            this.documentation = this.comment;
        }
        this.proc0 = proc0 == null ? this : proc0;
        this.proc1 = proc1 == null ? this : proc1;
        this.proc2 = proc2 == null ? this : proc2;
        this.proc3 = proc3 == null ? this : proc3;
        this.procN = procN == null ? this : procN;
    }

    /**
     * Creates a primitive that provides one of the apply methods by subclassing.
     *
     * @param name          procedure name
     * @param definitionEnv definition Environment
     * @param keyword       keyword?
     * @param minArgs       minimum no. of arguments
     * @param maxArgs       maximum no. of arguments, or -1 for varargs
     * @param comment       short procedure description
     * @param documentation full procedure documentation
     */
    Primitive(String name,
              Environment.Kind definitionEnv,
              boolean keyword,
              int minArgs,
              int maxArgs,
              String comment,
              String documentation)
    {
        this(name,
             definitionEnv,
             keyword,
             minArgs,
             maxArgs,
             comment,
             documentation,
             null,
             null,
             null,
             null,
             null);
    }

    Primitive(String name,
              Environment.Kind definitionEnv,
              boolean keyword,
              int minArgs,
              int maxArgs,
              String comment,
              String documentation,
              Proc0 proc0)
    {
        this(name,
             definitionEnv,
             keyword,
             minArgs,
             maxArgs,
             comment,
             documentation,
             proc0,
             null,
             null,
             null,
             null);
    }

    Primitive(String name,
              Environment.Kind definitionEnv,
              boolean keyword,
              int minArgs,
              int maxArgs,
              String comment,
              String documentation,
              Proc1 proc1)
    {
        this(name,
             definitionEnv,
             keyword,
             minArgs,
             maxArgs,
             comment,
             documentation,
             null,
             proc1,
             null,
             null,
             null);
    }

    Primitive(String name,
              Environment.Kind definitionEnv,
              boolean keyword,
              int minArgs,
              int maxArgs,
              String comment,
              String documentation,
              Proc2 proc2)
    {
        this(name,
             definitionEnv,
             keyword,
             minArgs,
             maxArgs,
             comment,
             documentation,
             null,
             null,
             proc2,
             null,
             null);
    }

    Primitive(String name,
              Environment.Kind definitionEnv,
              boolean keyword,
              int minArgs,
              int maxArgs,
              String comment,
              String documentation,
              Proc3 proc3)
    {
        this(name,
             definitionEnv,
             keyword,
             minArgs,
             maxArgs,
             comment,
             documentation,
             null,
             null,
             null,
             proc3,
             null);
    }

    Primitive(String name,
              Environment.Kind definitionEnv,
              boolean keyword,
              int minArgs,
              int maxArgs,
              String comment,
              String documentation,
              ProcN procN)
    {
        this(name, definitionEnv, keyword, minArgs, maxArgs, comment, documentation, null, null, null, null, procN);
    }

    /**
     * Apply this primitive to zero arguments.
     *
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the execution of this
     *                                   primitive
     */
    public Entity apply(Environment env,
                        Continuation cont) throws GleamException
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
     * @return an Entity, or null to signal that only the continuation has been modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the execution of this
     *                                   primitive
     */
    public Entity apply(Entity arg1,
                        Environment env,
                        Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply1 not implemented", null);
    }

    /**
     * Apply this primitive to at most two arguments.
     *
     * @param arg1 the first argument to this primitive, or null if not present
     * @param arg2 the second argument to this primitive, or null if not present
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the execution of this
     *                                   primitive
     */
    public Entity apply(Entity arg1,
                        Entity arg2,
                        Environment env,
                        Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply2 not implemented", null);
    }

    /**
     * Apply this primitive to at most three arguments.
     *
     * @param arg1 the first argument to this primitive, or null if not present
     * @param arg2 the second argument to this primitive, or null if not present
     * @param arg3 the third argument to this primitive, or null if not present
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the execution of this
     *                                   primitive
     */
    public Entity apply(Entity arg1,
                        Entity arg2,
                        Entity arg3,
                        Environment env,
                        Continuation cont) throws GleamException
    {
        throw new GleamException(this, "apply3 not implemented", null);
    }

    /**
     * Apply this primitive to a variable number of arguments (but more than three).
     *
     * @param args a Scheme list (a Pair) holding the arguments
     * @param env  the environment in which to apply the primitive
     * @param cont the current continuation
     *
     * @return an Entity, or null to signal that only the continuation has been modified.
     *
     * @throws gleam.lang.GleamException if any error is signaled during the execution of this
     *                                   primitive
     */
    public Entity apply(List args,
                        Environment env,
                        Continuation cont) throws GleamException
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
        sb.append(name).append(":").append(minArgs);
        if (minArgs != maxArgs) {
            sb.append("..");
            if (maxArgs < 0) {
                sb.append("*");
            }
            else {
                sb.append(maxArgs);
            }
        }
        return sb.toString();
    }
}
