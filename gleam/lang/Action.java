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
 * Action.java
 *
 * Created on January, 19 2002, 12.08
 */

package gleam.lang;

/**
 * An abstract execution unit. This object represents a single program
 * fragment, which can be invoked during the execution of the program.
 * It can be seen as a partial continuation, because a full continuation
 * consists of a sequence of invocations of these units.
 * Actions are linked together in a tree-like structure, where an execution
 * goes from leaf to root. In a simple program, the tree may have the shape
 * of a simple list; but when continuations are captured and re-used,
 * the proper tree-shape may appear.
 */
public abstract class Action implements java.io.Serializable {
	/** tree structure */
	Action parent;
	
	/**
	 * 
	 * @param action 
	 * @return 
	 */
	public Action append(Action action) {
		action.parent = this.parent;
		this.parent = action;
		return action;
	}

	/**
	 * Executes this action with an argument and a continuation,
	 * returning a value, and advancing the continuation to the
	 * next action.
	 * 
	 * @param arg the argument to this step of execution
	 * @param cont the current continuation; this method should update
	 * 	the continuation's action to be the next action in the chain
	 * 	of execution, so to go forward in the program execution
	 * @return Entity
	 */
	abstract Entity invoke(Entity arg, Continuation cont)
		throws gleam.lang.GleamException;

}
