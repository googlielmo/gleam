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

import java.util.*;

/**
 * Scheme closure, a procedure with a definition environment.
 *
 */
public class Closure extends Procedure
{
	/* TODO: should implement scan-out of defines, as follows:
	 * when a closure is created, a scanOut method should
	 * retrieve a proper list of internally defined variables,
	 * i.e. for (lambda (x y)
	 *            (define a 1)
	 *            (define b (foo x y))
	 *            (begin
	 *              (define c 1)
	 *              (define d 2))
	 *            (b (+ a c d)))
	 * the scan out should give (a b c d).
	 * At application time, after creating the local environment,
	 * the scanned-out variables should be bound in the new
	 * environment with an Undefined value.
	 * This should suffice to preserve the "simultaneous definition"
	 * semantics of Scheme defines inside procedures.
	 *
	 * The scan-out could be either done internally by the existing
	 * constructor, or externally by the interpreter and passed on
	 * to a new constructor.
	 */
	protected Entity param;
	protected Pair body;
	protected Environment definitionenv;

	/**
	 * Constructor.
	 */
	public Closure(Entity param, Pair body, Environment env)
	{
		this.param = param;
		this.body = body;
		this.definitionenv = env;
	}

	/**
	 * Applies this closure.
	 */
	public Entity apply(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Environment localenv = new Environment(definitionenv);
		Entity currparam = param;
		Pair prev = null;
		boolean arglist = false;
		gleam.util.Report.println(4, "apply: ARGS = ", args);

		/* bind actual arguments to formals (long)
		 */
		try {
			// for each passed arg
			while (args != EmptyList.value) {
				// get next already-evaluated arg
				Entity obj = args.car;

				if (arglist == false) {
					if (currparam == EmptyList.value) {
						throw new GleamException("apply: too many arguments", this);
					}
					else if (currparam instanceof Pair) {
						// normal case: get param symbol
						// and bind it to argument in
						// local env
						Entity p = ((Pair)currparam).car;
						if (p instanceof Symbol) {
							localenv.define((Symbol)p, obj);
						}
						else {
							gleam.util.Report.println(5, "apply: param is not a symbol");
						}
						// next param, please
						currparam = ((Pair)currparam).cdr;
					}
					else if (currparam instanceof Symbol) {
						// this is the unusual case
						// we have a "." notation parameter
						// so we accumulate this and next
						// parameters in a cons bound to
						// this param in local env
						prev = new Pair(obj, EmptyList.value);
						localenv.define((Symbol) currparam, prev);
						arglist = true;
					}
					else {
						throw new GleamException("apply: invalid formal parameter", currparam);
					}
				}
				else {
					// accumulate argument
					prev.cdr = new Pair(obj, EmptyList.value);
					prev = (Pair)prev.cdr;
				}
				// next argument, please
				args = (Pair)args.cdr;
			}
		}
		catch (ClassCastException e) {
			throw new GleamException("apply: improper list", currparam);
		}

		if (currparam instanceof Symbol && !arglist) {
			// special case:
			// a "." notation parameter taking the empty list
			localenv.define((Symbol) currparam, EmptyList.value);
		}
		else if (currparam != EmptyList.value && !arglist) {
			throw new GleamException("apply: too few arguments", this);
		}

		/* now we have bound params, let's eval body
		 * through a new continuation
		 */
		cont.action = addCommandSequenceActions(body, localenv, cont.action);
		return Void.value;
	}

	/**
	 * addCommandSequenceActions
	 *
	 * @param body Pair
	 * @param env Environment
	 * @param action Action
	 * @return Action
	 */
	public static Action addCommandSequenceActions(Pair body, Environment env,
						Action action)
		throws GleamException
	{
		Action  currAction = action;
		java.util.ArrayList temp = new java.util.ArrayList();
		ListIterator it = new ListIterator(body);
		while (it.hasNext()) {
			Entity expr = (Entity) it.next();
			temp.add(0, expr);
		}
		for (Iterator iter = temp.iterator(); iter.hasNext(); ) {
			Entity expr = (Entity) iter.next();
			currAction = new ExpressionAction(expr, env, currAction);
		}
		return currAction;
	}

	/**
	 * Writes a Closure
	 */
	public void write(java.io.PrintWriter out)
	{
		Pair extrep = new Pair(Symbol.LAMBDA, new Pair(param, body));
		out.write("#<procedure ");
		extrep.write(out);
		out.write(">");
	}
}
