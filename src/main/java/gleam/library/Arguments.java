/*
 * Copyright (c) 2023 Guglielmo Nigri.  All Rights Reserved.
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

import gleam.lang.Closure;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.List;
import gleam.lang.MutableString;
import gleam.lang.OutputPort;
import gleam.lang.Pair;
import gleam.lang.Procedure;
import gleam.lang.Symbol;

public class Arguments
{
    public static gleam.lang.Character requireCharacter(String context,
                                                        Entity arg) throws GleamException
    {
        if (arg instanceof gleam.lang.Character) {
            return (gleam.lang.Character) arg;
        }
        throw new GleamException(context + ": not a character", arg);
    }

    public static Closure requireClosure(String context, Entity arg) throws GleamException
    {
        if (arg instanceof Closure) {
            return (Closure) arg;
        }
        throw new GleamException(context + ": not a closure", arg);
    }

    public static Environment requireEnvironment(String context, Entity arg) throws GleamException
    {
        if (arg instanceof Environment) {
            return (Environment) arg;
        }
        throw new GleamException(context + ": not an environment", arg);
    }

    public static InputPort requireInputPort(String context, Entity arg) throws GleamException
    {
        if (arg instanceof InputPort) {
            return (InputPort) arg;
        }
        throw new GleamException(context + ": not an input port", arg);
    }

    public static OutputPort requireOutputPort(String context, Entity arg) throws GleamException
    {
        if (arg instanceof OutputPort) {
            return (OutputPort) arg;
        }
        throw new GleamException(context + ": not an output port", arg);
    }

    public static List requireList(String context, Entity arg) throws GleamException
    {
        if (arg instanceof List) {
            return (List) arg;
        }
        throw new GleamException(context + ": not a proper list", arg);
    }

    public static MutableString requireMutableString(String context,
                                                     Entity arg) throws GleamException
    {
        if (arg instanceof MutableString) {
            return (MutableString) arg;
        }
        throw new GleamException(context + ": not a string", arg);
    }

    public static gleam.lang.Number requireNumber(String context, Entity arg) throws GleamException
    {
        if (arg instanceof gleam.lang.Number) {
            return (gleam.lang.Number) arg;
        }
        throw new GleamException(context + ": not a number", arg);
    }

    public static Pair requirePair(String context, Entity arg) throws GleamException
    {
        if (arg instanceof Pair) {
            return (Pair) arg;
        }
        throw new GleamException(context + ": not a pair", arg);
    }

    public static Procedure requireProcedure(String context, Entity arg) throws GleamException
    {
        if (arg instanceof Procedure) {
            return (Procedure) arg;
        }
        throw new GleamException(context + ": not a procedure", arg);
    }

    public static Symbol requireSymbol(String context, Entity arg) throws GleamException
    {
        if (arg instanceof Symbol) {
            return (Symbol) arg;
        }
        throw new GleamException(context + ": not a symbol", arg);
    }

    /** Can't instantiate this class. */
    private Arguments() {}
}
