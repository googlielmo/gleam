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
 * IfAction.java
 *
 * Created on January 24, 2002, 20.57
 */

package gleam.lang;

/**
 * If (conditional expression) action.
 */
public class IfAction extends Action
{

    private static final long serialVersionUID = 2L;

    /** consequent, alternate parts of the if command */
    protected final Entity consequent, alternate;

    public IfAction(Entity consequent, Entity alternate, Environment env)
    {
        super(env);
        this.consequent = consequent;
        this.alternate = alternate;
    }

    /**
     * Evaluates the consequent or the alternate, deciding upon the truth value of the argument.
     * <p>
     * If it is any value except a boolean <code>false</code>, then the consequent is evaluated;
     * otherwise the alternate is.
     *
     * @param arg  the value upon which the decision is taken
     * @param cont the current Continuation
     *
     * @return the result of the evaluation
     */
    @Override
    Entity invoke(Entity arg, Continuation cont)
    {
        cont.beginWith(
                new ExpressionAction(
                        arg != Boolean.falseValue ? consequent : alternate,
                        env));

        return null;
    }
}
