/*
 * Copyright (c) 2002-2023 Guglielmo Nigri.  All Rights Reserved.
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

/*
 * AssignmentAction.java
 *
 * Created on January 24, 2002, 21.14
 */

package gleam.lang;

/**
 * Assignment action
 */
public class AssignmentAction extends Action
{

    private static final long serialVersionUID = 2L;

    /** The symbol to assign to. */
    protected final Symbol symbol;

    /** Creates a new AssignmentAction. */
    public AssignmentAction(Symbol symbol, Environment env)
    {
        super(env);
        this.symbol = symbol;
    }

    /**
     * Invokes this action, causing an assignment of the argument to the symbol in the provided
     * environment (with the mutation semantics of
     * <code>set!</code>)
     *
     * @param arg  the value to assign to the symbol in this action
     * @param cont the current Continuation
     *
     * @return the <code>Void</code> singleton
     *
     * @throws gleam.lang.GleamException in case of errors
     */
    @Override
    Entity invoke(Entity arg, Continuation cont) throws GleamException
    {
        env.getLocation(symbol).set(arg);
        trace(out -> out.printf("%s <- %s\n",
                                symbol.toWriteFormat(),
                                arg.toWriteFormat()), env);
        return Void.VALUE;
    }
}
