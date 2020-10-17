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

package gleam.library;

import gleam.lang.ArgumentList;
import gleam.lang.Boolean;
import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.List;
import gleam.lang.Pair;
import gleam.lang.Procedure;
import gleam.lang.ProcedureCallAction;

/**
 * CONTROL FEATURES
 * Primitive operator and procedure implementation library.
 */
public final class ControlFeatures {

    /**
     * Can't instantiate this class
     */
    private ControlFeatures() {}

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the three initial environments.
     */
    public static Primitive[] primitives = {

    /**
     * procedure?
     * Tests if argument is a procedure
     */
    new Primitive( "procedure?",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Returns true if argument is a procedure, false otherwise",
        "E.g. (procedure? cons) => #t" /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
    {
        return Boolean.makeBoolean(arg1 instanceof Procedure);
    }},

    /**
     * call-with-current-continuation
     */
    new Primitive( "call-with-current-continuation",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Calls a procedure with an escape procedure arg.",
        "Also known as call/cc, this operator is both unusual and powerful.\n"+
        "A simple usage pattern of call/cc is to implement exception handling." /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        if (arg1 instanceof Procedure) {
            /* create a new procedure call with the continuation argument. */
            ArgumentList arglist = new ArgumentList();
            arglist.set(0, new Continuation(cont)); // copy-constructor: cont itself is going to change soon!
            cont.begin(new ProcedureCallAction(arglist, env));
            return arg1;
        }
        else {
            throw new GleamException("call-with-current-continuation: wrong argument type, should be a procedure", arg1);
        }
    }},

    /**
     * apply
     */
    new Primitive( "apply",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, 2, /* min, max no. of arguments */
        null, null /* doc strings */ ) {
    @Override
    public Entity apply2(Entity proc, Entity args, Environment env, Continuation cont)
        throws GleamException
    {
        if (!(proc instanceof Procedure)) {
            throw new GleamException(this, "wrong argument type, should be a procedure", proc);
        }

        if (args instanceof List) {
            /* create a new procedure call with the given arguments. */
            ArgumentList argList = new ArgumentList((List) args);
            cont.begin(new ProcedureCallAction(argList, env));
            return proc;
        }

        throw new GleamException(this, "wrong argument type, should be a list", args);
    }},

    }; // primitives

}
