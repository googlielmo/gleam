/*
 * Copyright (c) 2008-2023 Guglielmo Nigri.  All Rights Reserved.
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
 * ExpressionInEnvironmentAction.java
 *
 * Created on January 19, 2008, 19.32
 */

package gleam.lang;

/**
 * An Action that evaluates an expression in an arbitrary environment.
 */
public class ExpressionInEnvironmentAction extends Action
{

    private static final long serialVersionUID = 2L;

    /** the expression to evaluate */
    private Entity expr;

    /** Creates a new instance of this action */
    public ExpressionInEnvironmentAction(Entity expr, Environment env)
    {
        this(expr, env, null);
    }

    /** Creates a new instance of this action */
    public ExpressionInEnvironmentAction(Entity expr,
                                         Environment env,
                                         Action next)
    {
        super(env, next);
        this.expr = expr;
    }

    /**
     * Invokes this action, causing the evaluation of the expression in the
     * environment passed as argument.
     *
     * @param newEnv the environment in which to evaluate the expression
     * @param cont   the current Continuation
     *
     * @return the result of the evaluation
     *
     * @throws gleam.lang.GleamException in case of errors
     */
    @Override
    Entity invoke(Entity newEnv,
                  Continuation cont) throws gleam.lang.GleamException
    {
        cont.head = next;
        if (!(newEnv instanceof Environment)) {
            throw new GleamException("not an environment", newEnv);
        }
        Environment evalEnv = (Environment) newEnv;
        expr = expr.analyze(evalEnv).optimize(evalEnv);
        trace(out -> out.printf("%s\n", expr.toWriteFormat()), env);
        return expr.eval(evalEnv, cont);
    }
}
