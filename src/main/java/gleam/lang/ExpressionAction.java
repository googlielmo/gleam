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
 * ExpressionAction.java
 *
 * Created on January, 19 2002, 14.01
 */

package gleam.lang;

/**
 * An Action that evaluates an expression.
 */
public class ExpressionAction extends Action {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** the expression to evaluate */
    private final Entity expr;

    /** the environment in which to evaluate the expression */
    private final Environment env;

    /** Creates a new instance of this action */
    public ExpressionAction(Entity expr, Environment env, Action next) {
        this.expr = expr;
        this.env = env;
        this.next = next;
    }

    /** Creates a new instance of this action */
    public ExpressionAction(Entity value, Environment env) {
        this(value, env, null);
    }

    /** Invokes this action, causing the evaluation of the expression
     * @param arg is ignored
     * @param cont the current Continuation
     * @return the result of the evaluation
     * @throws gleam.lang.GleamException in case of errors
    */
    @Override
    Entity invoke(Entity arg, Continuation cont) throws gleam.lang.GleamException
    {
        cont.head = next;
        // note: ignore arg
        return expr.eval(env, cont);
    }
}
