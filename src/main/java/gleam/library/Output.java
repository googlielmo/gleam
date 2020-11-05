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

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.OutputPort;
import gleam.lang.Void;

/**
 * Output
 * Primitive operator and procedure implementation library.
 */
public final class Output {

    /**
     * Can't instantiate this class
     */
    private Output() {
    }

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the initial environments.
     */
    public static final Primitive[] primitives = {

    /*
     * display
     * Displays an object
     */
    new Primitive( "display",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 2, /* min, max no. of arguments */
        "Writes an object in human-readable form, e.g. (display \"hello\")",
        null /* doc strings */ ) {
    @Override
    public Entity apply2(Entity obj, Entity obj2, Environment env, Continuation cont)
        throws GleamException
    {
        OutputPort out;

        // get output port, if present
        if (obj2 != null) {
            if (obj2 instanceof OutputPort) {
                out = (OutputPort) obj2;
            }
            else {
                throw new GleamException(this, "not an output port", obj2);
            }
        }
        else {
            out = Interpreter.getInterpreter().getCout();
        }

        // print object
        if (out.isOpen()) {
            out.display(obj);
            return Void.value();
        }
        else {
            throw new GleamException(this, "closed output port", out);
        }
    }},

    /*
     * write
     * Writes an object
     */
    new Primitive( "write",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 2, /* min, max no. of arguments */
        "Writes an object in machine-readable form, e.g. (write \"hello\")",
        null /* doc strings */ ) {
    @Override
    public Entity apply2(Entity arg1, Entity arg2, Environment env, Continuation cont)
        throws GleamException
    {
        // get output port, if present
        OutputPort oport = getOutputPort(this, arg2);

        // print object
        if (oport.isOpen()) {
            oport.write(arg1);
            return Void.value();
        }
        else {
            throw new GleamException(this, "closed output port", oport);
        }
    }},

    /*
     * newline
     * Writes an end of line
     */
    new Primitive( "newline",
        Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, 1, /* min, max no. of arguments */
        "Writes an end of line to the current or specified output port",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        OutputPort oport = getOutputPort(this, arg1);

        if (oport.isOpen()) {
            oport.newline();
            return Void.value();
        }
        else {
            throw new GleamException(this, "closed output port", oport);
        }
    }},

    /*
     * newline
     * Writes an end of line
     */
    new Primitive( "flush",
            Primitive.R5RS_ENV, Primitive.IDENTIFIER, /* environment, type */
            0, 1, /* min, max no. of arguments */
            "Flushes the current or specified output port",
            null /* doc strings */ ) {
        @Override
        public Entity apply1(Entity arg1, Environment env, Continuation cont)
                throws GleamException
        {
            OutputPort oport = getOutputPort(this, arg1);

            if (oport.isOpen()) {
                oport.flush();
                return Void.value();
            }
            else {
                throw new GleamException(this, "closed output port", oport);
            }
        }
    },

    }; // primitives

    private static OutputPort getOutputPort(Primitive primitive, Entity entity) throws GleamException {
        OutputPort oport;
        if (entity != null) {
            if (entity instanceof OutputPort) {
                oport = (OutputPort) entity;
            }
            else {
                throw new GleamException(primitive, "not an output port", entity);
            }
        }
        else {
            oport = Interpreter.getInterpreter().getCout();
        }
        return oport;
    }
}
