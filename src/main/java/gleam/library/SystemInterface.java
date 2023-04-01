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

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.MutableString;
import gleam.lang.Void;

import java.io.IOException;

import static gleam.lang.Environment.Kind.REPORT_ENV;

/**
 * SYSTEM INTERFACE
 * <p>
 * Primitive operator and procedure implementation library.
 */
public final class SystemInterface
{

    /**
     * This array contains definitions of primitives. It is used by static initializers in
     * gleam.lang.System to populate the three initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * load
             * Loads and executes an external source file
             */
            new Primitive("load",
                          REPORT_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Loads and executes a source file",
                          null /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    MutableString filename;
                    try {
                        filename = (MutableString) arg1;
                    }
                    catch (ClassCastException e) {
                        throw new GleamException(this,
                                                 "argument is not a string",
                                                 arg1);
                    }
                    try (InputPort iport = new InputPort(filename.toString())) {
                        env.getExecutionContext().getInterpreter().load(iport, env);
                        return Void.VALUE;
                    }
                    catch (IOException e) {
                        throw new GleamException(this, "file not found", arg1);
                    }
                }
            }

    }; // primitives

    /** Can't instantiate this class. */
    private SystemInterface() {}

}
