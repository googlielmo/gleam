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

import gleam.lang.Boolean;
import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.JavaObject;

import static gleam.lang.Environment.Kind.REPORT_ENV;

/**
 * Primitive operator and procedure implementation library.
 */
public final class Equivalence {

    /**
     * Can't instantiate this class
     */
    private Equivalence() {}

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the three initial environments.
     */
    public static final Primitive[] primitives = {

    /*
     * eq?
     * Compares arguments by address.
     */
    new Primitive( "eq?",
        REPORT_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, 2, /* min, max no. of arguments */
        "True if two objects are the same in memory, false otherwise",
        "E.g. (eq? 'a 'a) => #t, but (eq? (list 'a) (list 'a)) => #f" /* doc strings */ ) {
    @Override
    public Entity apply2(Entity arg1, Entity arg2, Environment env, Continuation cont)
    {
        // Java object are a special case, since we want to compare the
        // underlying objects to preserve common Java semantics
        if (arg1 instanceof JavaObject && arg2 instanceof JavaObject)
            return Boolean.makeBoolean(((JavaObject) arg1).eq_p((JavaObject) arg2));
        else
            return Boolean.makeBoolean(arg1 == arg2);
    }},

    /*
     * eqv?
     * Compares arguments by value or address.
     */
    new Primitive( "eqv?",
        REPORT_ENV, Primitive.IDENTIFIER, /* environment, type */
        2, 2, /* min, max no. of arguments */
        "True if two objects have equivalent values, false otherwise",
        "E.g. (eqv? 10 10) => #t" /* doc strings */ ) {
    @Override
    public Entity apply2(Entity arg1, Entity arg2, Environment env, Continuation cont)
    {
        return Boolean.makeBoolean(arg1.equals(arg2));
    }},

    }; // primitives
}
