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

package gleam.lang;

import gleam.library.Primitive;

/**
 * Scheme primitive library procedure.
 */
public class PrimitiveProcedure extends Procedure
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    protected Primitive value;

    /**
     * PrimitiveProcedure
     */
    public PrimitiveProcedure(gleam.library.Primitive primitive) {
        this.value = primitive;
    }

    public Entity apply(Pair arg, Environment env, Continuation cont)
        throws GleamException
    {
        if (value.maxArgs < 0 || value.maxArgs > 3) {
            if (value.minArgs >= 0 || value.maxArgs >= 0) {
                checkNumArgs(arg);
            }
            return value.applyN(arg, env, cont);
        }
        // ok, 0 <= maxArgs <= 3 : special rules
        assert 0 <= value.maxArgs && value.maxArgs <= 3; // DEBUG
        Entity[] argArray = new Entity[] {null, null, null};
        int countedArgs = 0;
        ListIterator it = new ListIterator(arg);
        while (it.hasNext()) {
            argArray[countedArgs++] = it.next();
            if (countedArgs > value.maxArgs) {
                throw new GleamException(value, "too many arguments", arg);
            }
        }
        if (countedArgs < value.minArgs) {
            throw new GleamException(value, "too few arguments", arg);
        }
        switch (value.maxArgs) {
            case 0:
                return value.apply0(env, cont);
            case 1:
                return value.apply1(argArray[0], env, cont);
            case 2:
                return value.apply2(argArray[0], argArray[1], env, cont);
            case 3:
                return value.apply3(argArray[0], argArray[1], argArray[2], env, cont);
            default: // DEBUG CANNOT HAPPEN
                assert false;
                return null;
        }
    }

    public void write(java.io.PrintWriter out)
    {
        out.write("#<primitive-procedure "+ value.toString() + ">");
    }

    private void checkNumArgs(Pair args) throws GleamException {
        ListIterator it = new ListIterator(args);
        int i;
        for (i = 0; i < value.minArgs; ++i) {
            if (!it.hasNext()) {
                throw new GleamException(value, "too few arguments", args);
            }
            it.next();
        }
        if (value.maxArgs > 0) {
            while (it.hasNext()) {
                if (++i > value.maxArgs)
                    throw new GleamException(value, "too many arguments", args);
            }
        }
    }

}
