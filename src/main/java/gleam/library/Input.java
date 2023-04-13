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
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.Eof;
import gleam.lang.GleamException;
import gleam.lang.InputPort;

import static gleam.lang.Environment.Kind.REPORT_ENV;
import static gleam.library.Primitive.IDENTIFIER;

/**
 * INPUT
 * <p>
 * Primitive operator and procedure implementation library.
 */
public final class Input
{

    /**
     * This array contains definitions of primitives. It is used by static initializers in
     * gleam.lang.System to populate the three initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * eof-object?
             * Tests if argument is an EOF object
             */
            new Primitive("eof-object?",
                          REPORT_ENV,
                          IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns true if argument is the EOF object, false otherwise",
                          null /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont)
                {
                    return Boolean.makeBoolean(arg1 instanceof Eof);
                }
            },

            /*
             * read
             * Reads an object
             */
            new Primitive("read",
                          REPORT_ENV,
                          IDENTIFIER, /* environment, type */
                          0,
                          1, /* min, max no. of arguments */
                          "Reads an object from the current or specified input port",
                          null /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    InputPort iport = getInputPort(this, arg1, env);
                    return iport.read();
                }
            },

            /*
             * read-char
             * Reads a character from the current input port
             */
            new Primitive("read-char",
                          REPORT_ENV, /* environment */
                          IDENTIFIER, /* type */
                          0, /* min no. of arguments */
                          1, /* max no. of arguments */
                          "Returns the next character available from the input port", /* comment */
                          null /* docs */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    InputPort iport = getInputPort(this, arg1, env);
                    return iport.readChar();
                }
            },

            /*
             * peek-char
             * Reads a character from the current input port
             */
            new Primitive("peek-char",
                          REPORT_ENV, /* environment */
                          IDENTIFIER, /* type */
                          0, /* min no. of arguments */
                          1, /* max no. of arguments */
                          "Returns the next character available from the input port, without " +
                          "advancing the current character", /* comment */
                          null /* docs */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    InputPort iport = getInputPort(this, arg1, env);
                    return iport.peekChar();
                }
            }

    }; // primitives

    private static InputPort getInputPort(Primitive primitive,
                                          Entity arg1,
                                          Environment env) throws GleamException
    {
        InputPort iport;
        if (arg1 == null) {
            iport = env.getExecutionContext().getIn();
        }
        else {
            if (!(arg1 instanceof InputPort)) {
                throw new GleamException(primitive, "not an input port", arg1);
            }
            iport = (InputPort) arg1;
        }
        return iport;
    }

    /** Can't instantiate this class. */
    private Input() {}
}
