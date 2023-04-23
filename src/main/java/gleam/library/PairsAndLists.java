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

import gleam.lang.Boolean;
import gleam.lang.Continuation;
import gleam.lang.EmptyList;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.List;
import gleam.lang.ListIterator;
import gleam.lang.Pair;
import gleam.lang.Void;

import static gleam.lang.Environment.Kind.REPORT_ENV;
import static gleam.library.Arguments.requireList;
import static gleam.library.Arguments.requirePair;

/**
 * PAIRS AND LISTS
 * <p>
 * Primitive operator and procedure implementation library.
 */
public final class PairsAndLists
{

    /**
     * This array contains definitions of primitives. It is used by static initializers in
     * gleam.lang.System to populate the three initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * car
             * Takes the first element of a pair.
             */
            new Primitive("car",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Gets first object in a pair, e.g. (car (list 1 2 3))",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(Entity arg1,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    return requireList("car", arg1).getCar();
                }
            },

            /*
             * cdr
             * Takes the second element of a pair.
             */
            new Primitive("cdr",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Gets second object in a pair, e.g. (cdr (list 1 2 3))",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(Entity arg1,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    return requireList("cdr", arg1).getCdr();
                }
            },

            /*
             * cons
             * Creates a new pair.
             */
            new Primitive("cons",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          2,
                          2, /* min, max no. of arguments */
                          "Creates a new pair, e.g. (cons 1 (cons 2 '(3)))",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(Entity first,
                                    Entity second,
                                    Environment env,
                                    Continuation cont)
                {
                    return new Pair(first, second);
                }
            },

            /*
             * list
             * Creates a new list from its arguments.
             */
            new Primitive("list",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          0,
                          Primitive.VAR_ARGS, /* min, max no. of arguments */
                          "Creates a new list from its arguments, e.g. (list 1 2 3)",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(List args,
                                    Environment env,
                                    Continuation cont)
                {
                    // TODO: could we simply return list?
                    ListIterator it = new ListIterator(args);
                    if (!it.hasNext()) {
                        return EmptyList.VALUE;
                    }
                    Pair l = new Pair(it.next(), EmptyList.VALUE);
                    Pair ins = l;
                    while (it.hasNext()) {
                        Pair nextcons = new Pair(it.next(), EmptyList.VALUE);
                        ins.setCdr(nextcons);
                        ins = nextcons;
                    }
                    return l;
                }
            },

            /*
             * pair?
             * Tests if argument is a pair
             */
            new Primitive("pair?",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns true if argument is a pair, false otherwise",
                          "E.g. (pair? (cons 1 2)) => #t" /* doc strings */)
            {
                @Override
                public Entity apply(Entity obj,
                                    Environment env,
                                    Continuation cont)
                {
                    return Boolean.makeBoolean(obj instanceof Pair);
                }
            },

            /*
             * null?
             * Tests if argument is the empty list
             */
            new Primitive("null?",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns true if argument is the empty list, false otherwise",
                          "E.g. (null? '()) => #t" /* doc strings */)
            {
                @Override
                public Entity apply(Entity obj,
                                    Environment env,
                                    Continuation cont)
                {
                    return Boolean.makeBoolean(obj instanceof EmptyList);
                }
            },

            /*
             * set-car!
             * store an object in the car field of a pair.
             */
            new Primitive("set-car!",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          2,
                          2, /* min, max no. of arguments */
                          "Sets car field in a pair, e.g. (set-car! my-pair 1)",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(Entity first,
                                    Entity second,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    requirePair("set-car!", first).setCar(second);
                    return Void.VALUE;
                }
            },

            /*
             * set-cdr!
             * store an object in the cdr field of a pair.
             */
            new Primitive("set-cdr!",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          2,
                          2, /* min, max no. of arguments */
                          "Sets cdr field in a pair, e.g. (set-cdr! my-pair 2)",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(Entity first,
                                    Entity second,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    requirePair("set-cdr!", first).setCdr(second);
                    return Void.VALUE;
                }
            }

    }; // primitives

    /** Can't instantiate this class. */
    private PairsAndLists() {}
}
