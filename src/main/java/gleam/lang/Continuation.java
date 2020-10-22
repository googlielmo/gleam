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

/*
 * Continuation.java
 *
 * Created on October 26, 2001, 9:01 PM
 */

/**
 * Scheme continuation, representing the "next things to do" for a procedure,
 * or the future execution path in a Gleam program.
 * This object is a wrapper for a chain of actions (partial continuations or
 * execution steps), and can be called as a procedure of one argument.
 */
public class Continuation extends Procedure
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Dummy action used as anchor to add actions
     */
    private static final Action DUMMY_ACTION = new Action() {
        @Override
        Entity invoke(Entity arg, Continuation cont) throws GleamException {
            throw new GleamException("internal error: dummy action invoked");
        }
    };

    public Action head;

    /** Constructor */
    Continuation() {
        this.head = null;
    }

    /** Copy constructor */
    public Continuation(Continuation other)
    {
        this.head = other.head;
    }

    /**
     * Clears this continuation (unwinds stack)
     */
    public void clear() {
        this.head = null;
    }

    /**
     * Change this continuation to begin with a given action.
     * Prepend a single action to this continuation's chain.
     * The action's next is changed to be the current head, therefore
     * <i>do not</i> pass an action that is already chained to other actions:
     * its <CODE>next</CODE> field will be overwritten.<BR>
     * You can safely add other actions <i>after</i> calling this method by using
     * {@link Action#andThen(Action)} on the action.<BR>
     * If you need to add a variable number of actions at the head of
     * this continuation, see {@link #beginSequence()}
     *
     * @param action the Action to prepend
     * @return the prepended action
     */
    public Action begin(Action action) {
        action.next = this.head;
        this.head = action;
        return action;
    }

    /**
     * Prepares for inserting a sequence of actions at the start of this continuation.
     * Append new actions to this method's return value with {@link Action#andThen(Action)}
     * Terminate the sequence by calling {@link #endSequence()}.
     * E.g.,
     * <BR><BR>
     * <CODE>
     *      Action action = cont.beginSequence();<BR>
     *      action = action.andThen(...).andThen(...);<BR>
     *          ...<BR>
     *      action = action.andThen(...);<BR>
     *          ...<BR>
     *      cont.endSequence();<BR>
     * </CODE>
     *
     * @return a "dummy" Action to append to
     * @see Action#andThen(Action)
     * @see #endSequence()
     */
    public Action beginSequence() {
        return begin(DUMMY_ACTION);
    }

    /**
     * Finalizes an insertion sequence started with {@link #beginSequence()}.
     * Removes the "dummy" action at the head of this continuation.
     */
    public void endSequence() {
        if (this.head == DUMMY_ACTION)
            this.head = this.head.next;
    }

    /**
     * addCommandSequenceActions
     *
     * @param body Pair
     * @param env Environment
     */
    public void addCommandSequenceActions(Iterable<Entity> body, Environment env)
    {
        Action currAction = beginSequence();
        for (Entity expr : body) {
            currAction = currAction.andThen(new ExpressionAction(expr, env));
        }
        endSequence();
    }

    /**
     * Applies this continuation.
     * Replaces the continuation in the current interpreter with this one.
     * Gets one argument, and returns it to the current interpreter as the
     * argument that this continuation will receive when executed, i. e.
     * immediately after the action of returning.
     *
     * @param args List
     * @param env Environment
     * @param cont Continuation
     * @return Entity
     */
    @Override
    public Entity apply(List args, Environment env, Continuation cont)
        throws GleamException
    {
        if (args != EmptyList.value) {
            if (args.getCdr() == EmptyList.value) {
                // replace continuation
                env.getInterpreter().replaceContinuation(this);
                // return argument (it's already evaluated)
                return args.getCar();
            }
            else {
                throw new GleamException("continuation: too many arguments", args);
            }
        }
        else {
            throw new GleamException("continuation: too few arguments", args);
        }
    }

    /**
     * Writes this continuation.
     */
    @Override
    public void write(java.io.PrintWriter out) {
        out.write("#<continuation>");
    }
}
