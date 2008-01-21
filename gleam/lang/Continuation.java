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

	public Action action;

	/** Constructor */
	Continuation() {
		this.action = null;
	}

	Continuation(Action what) {
		this.action = what;
	}

	public Continuation(Continuation other)
	{
		this.action = other.action;
	}

	/**
	 * 
	 * @param action 
	 * @return 
	 */
	public Action extend(Action action) {
		action.parent = this.action;
		this.action = action;
		return action;
	}

	/**
	 * Applies this continuation.
	 * Replaces the continuation in the current interpreter with this one.
	 * Gets one argument, and returns it to the current interpreter as the
	 * argument that this continuation will receive when executed, i. e.
	 * immediately after the action of returning.
	 *
	 * @param args Pair
	 * @param env Environment
	 * @param cont Continuation
	 * @throws gleam.lang.GleamException 
	 * @return Entity
	 */
	public Entity apply(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		if (args != EmptyList.value) {
			if (args.cdr == EmptyList.value) {
				// replace continuation
				env.getInterpreter().replaceContinuation(this);
				// return argument (it's already evaluated)
				return args.car;
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
	public void write(java.io.PrintWriter out) {
		out.write("#<continuation>");
	}
}
