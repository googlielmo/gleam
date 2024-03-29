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

package gleam.lang;

import gleam.library.Primitive;

import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Scheme primitive library procedure.
 */
public class PrimitiveProcedure extends Procedure
{

    private static final long serialVersionUID = 2L;

    protected final Primitive primitive;

    /**
     * PrimitiveProcedure
     */
    public PrimitiveProcedure(gleam.library.Primitive primitive)
    {
        this.primitive = primitive;
    }

    /**
     * Applies this primitive procedure to a list of arguments.
     * <p>
     * It is an error to invoke a primitive procedure with too few or too many arguments.
     *
     * @param args the list of arguments
     * @param env  the environment in which to execute the primitive
     * @param cont the continuation
     *
     * @throws GleamException in case of errors
     */
    @Override
    public Entity apply(List args,
                        Environment env,
                        Continuation cont) throws GleamException
    {
        // case of maxArgs >= 3 or varargs
        if (primitive.maxArgs < 0 || primitive.maxArgs > 3) {
            if (primitive.minArgs >= 0 || primitive.maxArgs >= 0) {
                checkNumArgs(args);
            }
            return primitive.procN.apply(args, env, cont);
        }
        // 0 <= maxArgs <= 3 : special rules
        Entity[] argArray = new Entity[]{null, null, null};
        int countedArgs = 0;
        ListIterator it = new ListIterator(args);
        while (it.hasNext()) {
            argArray[countedArgs++] = it.next();
            if (countedArgs > primitive.maxArgs) {
                throw new GleamException(primitive, "too many arguments", args);
            }
        }
        if (countedArgs < primitive.minArgs) {
            throw new GleamException(primitive, "too few arguments", args);
        }
        switch (primitive.maxArgs) {
            case 0:
                return primitive.proc0.apply(env, cont);
            case 1:
                return primitive.proc1.apply(argArray[0], env, cont);
            case 2:
                return primitive.proc2.apply(argArray[0], argArray[1], env, cont);
            default: // 3
                return primitive.proc3.apply(argArray[0], argArray[1], argArray[2], env, cont);
        }
    }

    private void checkNumArgs(List args) throws GleamException
    {
        Iterator<Entity> it = new ListIterator(args);
        int i;
        for (i = 0; i < primitive.minArgs; ++i) {
            if (!it.hasNext()) {
                throw new GleamException(primitive, "too few arguments", args);
            }
            it.next();
        }
        if (primitive.maxArgs > 0) {
            while (it.hasNext()) {
                if (++i > primitive.maxArgs) {
                    throw new GleamException(primitive, "too many arguments", args);
                }
            }
        }
    }

    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.write("#<primitive-procedure " + primitive.toString() + ">");
        return out;
    }
}
