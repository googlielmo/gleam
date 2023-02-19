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

/*
 * Action.java
 *
 * Created on January 19, 2002, 12.08
 */

package gleam.lang;

/**
 * Represents an abstract unit of execution, which can be invoked during the
 * program's execution. Execution units are a constituent of continuations, as a
 * full continuation consists of a sequence of invocations of these units.
 * Actions are linked together in a tree-like structure, where an execution goes
 * from leaf to root.
 * <p>
 * In a traditional program, the tree may degenerate into a list, taking the
 * role of the call stack used in many non tail-recursive languages. However,
 * when continuations are captured and re-used, their actions may end up being
 * arranged as a proper tree.
 */
public abstract class Action implements java.io.Serializable
{
    /** the environment in which to execute this action. */
    final Environment env;

    /** the next action to execute, this creates a tree structure. */
    Action next;

    /**
     * Invokes this action with an argument and a continuation, and returns a
     * value, advancing the continuation to the next action. Subclasses must
     * implement this method and update the continuation's head to the next
     * action to execute (e.g., {@code cont.head = next}) to move the program
     * execution forward. If the method only changes the continuation, and does
     * not return a value, it should return {@code null}.
     *
     * @param arg  the Entity argument to this step of execution
     * @param cont the current Continuation
     *
     * @return an Entity, or {@code null} to indicate that only the
     * continuation has been updated.
     *
     * @throws gleam.lang.GleamException in case of errors
     */
    abstract Entity invoke(Entity arg,
                           Continuation cont) throws gleam.lang.GleamException;

    protected interface Printer
    {
        void print(OutputPort port);
    }

    Action(Environment env, Action next)
    {
        this.env = env;
        this.next = next;
    }

    /**
     * Appends a new action after this one, so that the given action be executed
     * after this one.
     *
     * @param action the Action to append
     *
     * @return the argument
     *
     * @see Continuation#begin(Action)
     * @see Continuation#beginSequence()
     */
    public Action andThen(Action action)
    {
        action.next = this.next;
        this.next = action;
        return action;
    }

    protected void trace(Printer doo, Environment env)
    {
        if (env.getExecutionContext().isTraceEnabled()) {
            OutputPort cout = env.getExecutionContext().getOut();

            String actionName = this.getClass()
                                    .getSimpleName()
                                    .replace("Action", "");
            cout.printf("%s ", actionName);

            doo.print(cout);
        }
    }
}
