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

package gleam.library;

import gleam.lang.ArgumentList;
import gleam.lang.Boolean;
import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.List;
import gleam.lang.Procedure;
import gleam.lang.ProcedureCallAction;

import static gleam.lang.Environment.Kind.REPORT_ENV;

/**
 * CONTROL FEATURES
 * <p>
 * Primitive operator and procedure implementation library.
 */
public final class ControlFeatures
{

    /**
     * This array contains definitions of primitives. It is used by static initializers in
     * gleam.lang.System to populate the three initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * procedure?
             * Tests if argument is a procedure
             */
            new Primitive("procedure?",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns true if argument is a procedure, false otherwise",
                          "E.g. (procedure? cons) => #t" /* doc strings */)
            {
                @Override
                public Entity apply(Entity arg1,
                                    Environment env,
                                    Continuation cont)
                {
                    return Boolean.makeBoolean(arg1 instanceof Procedure);
                }
            },

            /*
             * call-with-current-continuation
             */
            new Primitive("call-with-current-continuation",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Calls a procedure with an escape procedure arg.",
                          "Also known as call/cc, this operator is both unusual and powerful.\n" +
                          "A simple usage pattern of call/cc is to implement exception handling." /* doc strings */)
            {
                @Override
                public Entity apply(Entity arg1,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    Arguments.requireProcedure("call-with-current-continuation: invalid argument",
                                               arg1);
                    /* create a new procedure call with the continuation argument. */
                    ArgumentList arglist = new ArgumentList();
                    /* use a copy of cont, as it's going to change */
                    arglist.set(0, new Continuation(cont));
                    cont.beginWith(new ProcedureCallAction(arglist, env));
                    return arg1;
                }
            },

            /*
             * apply
             */
            new Primitive("apply",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          2,
                          2, /* min, max no. of arguments */
                          "Calls a procedure with arguments, e.g. (apply + 1 2)",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(Entity proc,
                                    Entity args,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    Arguments.requireProcedure("apply", proc);
                    List argList = Arguments.requireList("apply: invalid arguments", args);
                    /* create a new procedure call with the given arguments. */
                    cont.beginWith(new ProcedureCallAction(new ArgumentList(argList), env));
                    return proc;
                }
            }

    }; // primitives

    /** Can't instantiate this class. */
    private ControlFeatures() {}
}
