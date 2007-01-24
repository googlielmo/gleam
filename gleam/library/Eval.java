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
import gleam.lang.Boolean;
import gleam.lang.Character;
import gleam.lang.Void;
import gleam.lang.Number;
 
import gleam.lang.*;

/**
 * EVAL
 * Primitive operator and procedure implementation library.
 */
public final class Eval {

	/**
	 * Can't instantiate this class
	 */
	private Eval() {
	}

	/**
	 * eval
	 * Evaluates an expression in an environment
	 */
	public static Entity gleam_eval(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity expr;
		Environment eval_env;
		ListIterator it = new ListIterator(args);	
		if (it.hasNext()) {
			expr = it.next();
			if (it.hasNext()) {
				try {
					eval_env = (Environment) it.next();
					if (it.hasNext()) {
						throw new GleamException("eval: too many arguments", args);
					}
				}
				catch (ClassCastException e) {
					throw new GleamException("eval: not an environment", args);
				}
			}
			else {
				eval_env = env;
			}
			expr = expr.analyze().optimize(eval_env);
			cont.extend(new ExpressionAction(expr, eval_env, null));
			return null;
		}
		else {
			throw new GleamException("eval: too few arguments", args);
		}
	}

	/**
	 * null-environment
	 * Returns the null environment
	 */
	public static Entity gleam_null_environment(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Number version;
		ListIterator it = new ListIterator(args);	
		if (it.hasNext()) {
			try {
				version = (Number) it.next();
				if (it.hasNext()) {
					throw new GleamException("null-environment: too many arguments", args);
				}
				if (version.getDoubleValue() == 4.0 || version.getDoubleValue() == 5.0) {
					return gleam.lang.System.getNullEnv();
				}
				else {
					throw new GleamException("null-environment: version not supported", version);
				}
			}
			catch (ClassCastException e) {
				throw new GleamException("null-environment: not a version number", args);
			}
		}
		else {
			throw new GleamException("null-environment: too few arguments", args);
		}
	}

	/**
	 * scheme-report-environment
	 * Returns the scheme-report environment
	 */
	public static Entity gleam_scheme_report_environment(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Number version;
		ListIterator it = new ListIterator(args);	
		if (it.hasNext()) {
			try {
				version = (Number) it.next();
				if (it.hasNext()) {
					throw new GleamException("scheme-report-environment: too many arguments", args);
				}
				if (version.getDoubleValue() == 4.0 || version.getDoubleValue() == 5.0) {
					return gleam.lang.System.getSchemeReportEnv();
				}
				else {
					throw new GleamException("scheme-report-environment: version not supported", version);
				}
			}
			catch (ClassCastException e) {
				throw new GleamException("scheme-report-environment: not a version number", args);
			}
		}
		else {
			throw new GleamException("scheme-report-environment: too few arguments", args);
		}
	}

	/**
	 * interaction-environment
	 * Returns the interaction environment
	 */
	public static Entity gleam_interaction_environment(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		if (args != EmptyList.makeEmptyList()) {
			throw new GleamException("interaction-environment: too many arguments", args);
		}

		return gleam.lang.System.getInteractionEnv();
	}

}