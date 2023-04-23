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

import static gleam.library.Arguments.requireProcedure;

/**
 * Procedure call action.
 * <p>
 * Wraps an argument list and an environment
 */
public class ProcedureCallAction extends Action
{

    private static final long serialVersionUID = 2L;

    private final ArgumentList arglist;

    public ProcedureCallAction(ArgumentList arglist, Environment env)
    {
        super(env);
        this.arglist = arglist;
    }

    /**
     * Calls a given procedure, an <i>operator</i>, with the argument list and in the environment
     * specified when this action was created.
     *
     * @param arg  the operator to call
     * @param cont the current continuation
     *
     * @throws gleam.lang.GleamException in cse of errors
     */
    @Override
    Entity invoke(Entity arg, Continuation cont) throws gleam.lang.GleamException
    {
        // apply function to args, return result
        Procedure procedure = requireProcedure("procedure call", arg);
        trace(out -> out.printf("%s\n", arg.toWriteFormat()), env);
        return procedure.apply(arglist.getArguments(), env, cont);
    }
}
