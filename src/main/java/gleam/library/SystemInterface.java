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

import gleam.lang.*;
import gleam.lang.Void;

/**
 * SystemInterface
 * Primitive operator and procedure implementation library.
 */
public final class SystemInterface {

    /**
     * Can't instantiate this class
     */
    private SystemInterface() {
    }

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the three initial environments.
     */
    public static Primitive[] primitives = {

    /**
     * load
     * Loads and executes an external source file
     */
    new Primitive( "load",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Loads and executes a source file",
        null /* doc strings */ ) {
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        try {
            MutableString filename = (MutableString) arg1;
            InputPort iport = new InputPort(filename.toString());
            env.getInterpreter().load(iport, env);
            return Void.value();
        }
        catch (ClassCastException e) {
            throw new GleamException(this, "argument is not a string", arg1);
        }
        catch (java.io.FileNotFoundException e) {
            throw new GleamException(this, "file not found", arg1);
        }
    }},
    
    }; // primitives
}
