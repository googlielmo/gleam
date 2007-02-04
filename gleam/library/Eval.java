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
	public static Entity gleam_eval_$1_2(Entity arg1, Entity arg2, Environment env, Continuation cont)
		throws GleamException
	{
		Environment eval_env;
		if (arg2 == null)
			eval_env = env;
		else try {
			eval_env = (Environment) arg2;
		}
		catch (ClassCastException e) {
			throw new GleamException("eval: not an environment", arg2);
		}
		arg1 = arg1.analyze().optimize(eval_env);
		cont.extend(new ExpressionAction(arg1, eval_env, null));
		return null;
	}

	/**
	 * null-environment
	 * Returns the null environment
	 */
	public static Entity gleam_null_environment_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		Number version;
		try {
			version = (Number) arg1;
			if (version.getDoubleValue() == 4.0 || version.getDoubleValue() == 5.0) {
				return gleam.lang.System.getNullEnv();
			}
			else {
				throw new GleamException("null-environment: version not supported", version);
			}
		}
		catch (ClassCastException e) {
			throw new GleamException("null-environment: not a version number", arg1);
		}
	}

	/**
	 * scheme-report-environment
	 * Returns the scheme-report environment
	 */
	public static Entity gleam_scheme_report_environment_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		Number version;
		try {
			version = (Number) arg1;
			if (version.getDoubleValue() == 4.0 || version.getDoubleValue() == 5.0) {
				return gleam.lang.System.getSchemeReportEnv();
			}
			else {
				throw new GleamException("scheme-report-environment: version not supported", version);
			}
		}
		catch (ClassCastException e) {
			throw new GleamException("scheme-report-environment: not a version number", arg1);
		}
	}

	/**
	 * interaction-environment
	 * Returns the interaction environment
	 */
	public static Entity gleam_interaction_environment_$0(Environment env, Continuation cont)
		throws GleamException
	{
		return gleam.lang.System.getInteractionEnv();
	}

}