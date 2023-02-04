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

/*
 * EvalAction.java
 *
 * Created on 11 dec 2006, 15.30
 */

package gleam.lang;

/**
 * Eval action.
 */
public class EvalAction extends Action
{

    private static final long serialVersionUID = 2L;

    /** Creates a new instance of this action. */
    public EvalAction(Environment env)
    {
        this(env, null);
    }

    /** Creates a new instance of this action. */
    public EvalAction(Environment env, Action next)
    {
        super(env, next);
    }

    /**
     * Invokes this action, causing the evaluation of its argument.
     *
     * @param arg  the Entity to evaluate
     * @param cont the current Continuation
     *
     * @return the result of the evaluation
     *
     * @throws gleam.lang.GleamException in case of errors
     */
    @Override
    Entity invoke(Entity arg, Continuation cont) throws GleamException
    {
        cont.head = next;
        trace(out -> out.printf("%s\n", arg.toWriteFormat()), env);
        return arg.eval(env, cont);
    }
}
