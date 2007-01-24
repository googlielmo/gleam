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

import gleam.lang.*;

/**
 * CONTROL FEATURES
 * Primitive operator and procedure implementation library.
 */
public final class ControlFeatures {

	/**
	 * Can't instantiate this class
	 */
	private ControlFeatures() {
	}

	/**
	 * procedure?
	 * Tests if argument is a procedure
	 */
	public static Entity gleam_procedure_p(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity obj = null;
		ListIterator it = new ListIterator(args);
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("procedure?: too few arguments", args);
		}

		if (!it.hasNext()) {
			return Boolean.makeBoolean(obj instanceof Procedure);
		}
		else {
			throw new GleamException("procedure?: too many arguments", args);
		}
	}

	/**
	 * call-with-current-continuation
	 */
	public static Entity gleam_callcc(Pair args, Environment env, Continuation cont)
		throws GleamException, CloneNotSupportedException
	{
		Entity obj = null;
		ListIterator it = new ListIterator(args);
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("call-with-current-continuation: too few arguments", args);
		}

		if (!it.hasNext()) {
			if (obj instanceof Procedure) {
				/* create a new procedure call
				 * with the continuation argument.
				 * note the copy-constructor: 
				 * cont itself is going to change soon!
				 */
				ArgumentList arglist = new ArgumentList();
				arglist.put(new Continuation(cont), 0);
				cont.action = new ProcedureCallAction(arglist, env, cont.action);
				return obj;
			}
			else {
				throw new GleamException("call-with-current-continuation: wrong argument type, should be a procedure", args);
			}
		}
		else {
			throw new GleamException("call-with-current-continuation: too many arguments", args);
		}
	}

}
