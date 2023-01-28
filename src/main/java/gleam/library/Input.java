/*
 * Copyright (c) 2001-2020 Guglielmo Nigri.  All Rights Reserved.
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
import gleam.lang.Eof;
import gleam.lang.GleamException;
import gleam.lang.InputPort;

import static gleam.lang.Environment.Kind.REPORT_ENV;

/**
 * Input
 * Primitive operator and procedure implementation library.
 */
public final class Input {

    /**
     * Can't instantiate this class
     */
    private Input() {
    }

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the three initial environments.
     */
    public static final Primitive[] primitives = {

    /*
     * eof-object?
     * Tests if argument is an EOF object
     */
    new Primitive( "eof-object?",
        REPORT_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Returns true if argument is the EOF object, false otherwise",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
    {
        return Boolean.makeBoolean(arg1 instanceof Eof);
    }},

    /*
     * read
     * Reads an object
     */
    new Primitive( "read",
        REPORT_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, 1, /* min, max no. of arguments */
        "Reads an object from the current or specified input port",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        try {
            InputPort iport;
            if (arg1 != null) {
                iport = (InputPort) arg1;
            }
            else {
                iport = env.getIn();
            }
            if (iport.isOpen()) {
                return iport.read();
            }
            else {
                throw new GleamException(this, "closed input port", arg1);
            }
        }
        catch (ClassCastException e) {
            throw new GleamException(this, "not an input port", arg1);
        }
    }},

    }; // primitives

}
