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
	public static Entity gleam_define_$2_N(Pair args, Environment env, Continuation cont)
	throws GleamException {
		try {
			ListIterator it = new ListIterator(args);
			Entity target = it.next();
			Entity value = it.next();
			/* see if it is a variable definition
			 * or a disguised lambda
			 * note that target is NOT evaluated
			 */
			if (target instanceof Symbol) {
				if (it.hasNext()) {
					throw new GleamException("define: too many arguments", args);
				}
				// TODO: check that target is not a keyword (?)
				Symbol s = (Symbol) target;
				// create binding
				env.define(s, Undefined.makeUndefined());
				// now it is equivalent to set!
				cont.extend(
					new ExpressionAction(value, env, null)).append(
					new AssignmentAction(s, env, null));

				return null;
			}
			else if (target instanceof Pair) {
				Entity rtarget = ((Pair)target).getCar();
				Entity params = ((Pair)target).getCdr();
				Pair body = new Pair(value, it.rest());
				if (rtarget instanceof Symbol) {
					Symbol s = (Symbol) rtarget;
					// create binding
					env.define(s, Undefined.makeUndefined());
					// equivalent to set!
					cont.extend(new AssignmentAction(s, env, null));
					return gleam_lambda_$2_N(new Pair(params, body), env, cont);
				}
				else {
					throw new GleamException("define: invalid procedure name", rtarget);
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
	public static Entity gleam_lambda_$2_N(Pair args, Environment env, Continuation cont)
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
	public static Entity gleam_if_$2_3(Entity test, Entity consequent, Entity alternate, Environment env, Continuation cont)
	throws GleamException {
		if (alternate == null)
			alternate = Void.makeVoid();

		cont.extend(
			new ExpressionAction(test, env, null)).append(
			new IfAction(consequent, alternate, env, null));

		return null;
//		cont.action = new IfAction(consequent, alternate, env, cont.action);
//		return test.eval(env, cont);
	}

	/**
	 * quote
	 * Returns its argument without evaluation.
	 */
	public static Entity gleam_quote_$1(Entity datum, Environment env, Continuation cont)
	throws GleamException {
		return datum;
	}

	/**
	 * set!
	 * Assigns a value to a variable
	 */
	public static Entity gleam_set_m_$2(Entity arg1, Entity obj, Environment env, Continuation cont)
	throws GleamException {
		try {
			Symbol s = (Symbol) arg1;
			cont.extend(
				new ExpressionAction(obj, env, null)).append(
				new AssignmentAction(s, env, null));

			return null;
//			cont.action = new AssignmentAction(s, env, cont.action);
//			return obj.eval(env, cont);
		}
		catch (ClassCastException e) {
			throw new GleamException("set!: argument is not a symbol", arg1);
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
		return null;
	}


	/**
	 * make-rewriter
	 */
	public static Entity gleam_make_rewriter_$1(Entity obj, Environment env, Continuation cont)
	throws GleamException {
		// evaluate argument: must be a function of exactly one argument
		// obj = obj.eval(env, cont);
		if (!(obj instanceof Closure)) {
			throw new GleamException(
				"make-rewriter: argument must be a function of one argument",
				obj);
		}
		else {
			Closure closure = (Closure) obj;
			// TODO: check closure arity == 1 // FIXME ?
			return new SyntaxRewriter(closure);
		}
	}


	/**
	 * rewrite1
	 */
	public static Entity gleam_rewrite1_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException {
		try {
			return gleam.lang.System.rewrite1((Pair) arg1, env);
		} catch (ClassCastException ex) {
			throw new GleamException(
				"rewrite1: argument is not a list", arg1);
		}
	}
}
