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

package gleam.library;

import gleam.lang.Entity;
import gleam.lang.System;
import gleam.lang.Void;

import gleam.lang.*;

/**
 * Primitive operator and procedure implementation library.
 */
public final class Syntax {

	/**
	 * Can't instantiate this class
	 */
	private Syntax() {
	}

	/**
	 * define
	 * Defines a new binding in the environment.
	 */
	public static Entity gleam_define(Pair args, Environment env, Continuation cont)
	throws GleamException {
		// TODO: check that args is not empty
		Entity target;
		try {
			target = args.getCar();

			/* see if it is a variable definition
			 * or a disguised lambda
			 * note that target is NOT evaluated
			 */
			if (target instanceof Symbol) {
				// TODO: check that target is not a keyword
				Entity value = ((Pair)args.getCdr()).getCar();
				if (((Pair)args.getCdr()).getCdr() != EmptyList.makeEmptyList()) {
					throw new GleamException("define: too many arguments", args);
				}
				Symbol s = (Symbol) target;
				// create binding
				env.define(s, Undefined.makeUndefined());
				// now it is equivalent to set!
				cont.action = new AssignmentAction(s, env, cont.action);
				return value.eval(env, cont);
			}
			else if (target instanceof Pair) {
				Entity rtarget = ((Pair)target).getCar();
				Entity params = ((Pair)target).getCdr();
				Entity body = (Pair)args.getCdr();
				if (rtarget instanceof Symbol) {
					Symbol s = (Symbol) rtarget;
					// create binding
					env.define(s, Undefined.makeUndefined());
					// equivalent to set!
					cont.action = new AssignmentAction(s, env, cont.action);
					return gleam_lambda(new Pair(params, body), env, cont);
				}
				else {
					throw new GleamException("define: invalid procedure name", args);
				}
			}
			else {
				throw new GleamException("define: invalid form", args);
			}
		}
		catch (ClassCastException e) {
			throw new GleamException("define: invalid arguments", args);
		}
	}

	/**
	 * lambda
	 * Creates a new procedure.
	 */
	public static Entity gleam_lambda(Pair args, Environment env, Continuation cont)
	throws GleamException {
		try {
			Entity lambdaParams = args.getCar();
			Pair lambdaBody = (Pair)args.getCdr();
			return new Closure(lambdaParams, lambdaBody, env);
		}
		catch (ClassCastException e) {
			throw new GleamException("lambda: invalid procedure definition", args);
		}
	}

	/**
	 * if
	 * Conditional expression.
	 */
	public static Entity gleam_if(Pair args, Environment env, Continuation cont)
	throws GleamException {
		try {
			Entity test = args.getCar();
			Pair xargs = ((Pair)args.getCdr());
			Entity consequent = xargs.getCar();
			Entity alternate = Void.makeVoid();
			if (xargs.getCdr() != EmptyList.makeEmptyList()) {
				alternate = ((Pair)xargs.getCdr()).getCar();
			}

			cont.action = new IfAction(consequent, alternate, env, cont.action);
			return test.eval(env, cont);
		}
		catch (ClassCastException e) {
			throw new GleamException("if: invalid arguments", args);
		}
		catch (GleamException e) {
			throw new GleamException("if: invalid arguments", args);
		}
	}

	/**
	 * quote
	 * Returns its argument without evaluation.
	 */
	public static Entity gleam_quote(Pair args, Environment env, Continuation cont)
	throws GleamException {
		// TODO: empty list
		try {
			Entity datum = args.getCar();
			if (args.getCdr() == EmptyList.makeEmptyList()) {
				return datum;
			}
			else {
				throw new GleamException("quote: too many arguments", args);
			}
		}
		catch (ClassCastException e) {
			throw new GleamException("quote: invalid arguments", args);
		}
	}

	/**
	 * set!
	 * Assigns a value to a variable
	 */
	public static Entity gleam_set_m(Pair args, Environment env, Continuation cont)
	throws GleamException {
		ListIterator it = new ListIterator(args);
		if (it.hasNext()) {
			try {
				Symbol s = (Symbol) it.next();
				if (it.hasNext()) {
					Entity obj = it.next();
					if (it.hasNext()) {
						throw new GleamException("set!: too many arguments", args);
					}
					cont.action = new AssignmentAction(s, env, cont.action);
					return obj.eval(env, cont);
				}
				else {
					throw new GleamException("set!: too few arguments", args);
				}
			}
			catch (ClassCastException e) {
				throw new GleamException("set!: not a symbol", args);
			}
		}
		else {
			throw new GleamException("set!: too few arguments", args);
		}
	}

	/**
	 * begin
	 * Evaluates each argument sequentially from left to right.
	 * The result of the last evaluation is returned.
	 */
	public static Entity gleam_begin(Pair args, Environment env, Continuation cont)
	throws GleamException {
		// equivalent to the body of a procedure with no arguments
		cont.action = gleam.lang.Closure.addCommandSequenceActions(args, env, cont.action);
		return Void.makeVoid();
	}


	/**
	 * make-rewriter
	 */
	public static Entity gleam_make_rewriter(Pair args, Environment env, Continuation cont)
	throws GleamException {
		Entity obj = null;
		ListIterator it = new ListIterator(args);
		if (it.hasNext()) {
			obj = it.next();
			if (!it.hasNext()) {
				// evaluate argument: must be a function of exactly one argument
				// obj = obj.eval(env, cont);
				if (!(obj instanceof Closure)) {
					throw new GleamException(
						"make-rewriter: argument must be a function of one argument",
						args);
				}
				else {
					Closure closure = (Closure) obj;
					// TODO: check closure arity == 1 // FIXME ?
					return new SyntaxRewriter(closure);
				}

			}
			else {
				throw new GleamException("make-rewriter: too many arguments", args);
			}
		}
		else {
			throw new GleamException("make-rewriter: too few arguments", args);
		}
	}


	/**
	 * rewrite1
	 */
	public static Entity gleam_rewrite1(Pair args, Environment env, Continuation cont)
		throws GleamException {
		if (args instanceof EmptyList) {
			throw new GleamException("rewrite1: too few arguments", args);
		}
		
		if (!(args.getCar() instanceof Pair && args.getCdr() instanceof EmptyList)) {
			throw new GleamException("rewrite1: invalid arguments", args);
		}
			
		return gleam.lang.System.rewrite1( (Pair) args.getCar(), env);
	}
}
