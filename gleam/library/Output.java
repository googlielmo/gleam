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
import gleam.lang.Number;
import gleam.lang.System;
import gleam.lang.Void;
 
import gleam.lang.*;

/**
 * Output
 * Primitive operator and procedure implementation library.
 */
public final class Output {

	/**
	 * Can't instantiate this class
	 */
	private Output() {
	}

	/**
	 * display
	 * Displays an object
	 */
	public static Entity gleam_display(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity obj = null, obj2;
		ListIterator it = new ListIterator(args);
		OutputPort out;

		// get object to print
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("display: too few arguments", args);
		}

		// get output port, if present
		if (it.hasNext()) {
			obj2 = it.next();
			if (obj2 instanceof OutputPort) {
				out = (OutputPort) obj2;
			}
			else {
				throw new GleamException("display: not an output port", args);
			}
		}
		else {
			out = System.getCout();
		}

		// print object
		if (!it.hasNext()) {
			if (out.isOpen()) {
				out.display(obj);
				return Void.makeVoid();
			}
			else {
				throw new  GleamException("display: closed output port", args);
			}
		}
		else {
			throw new GleamException("display: too many arguments", args);
		}
	}

	/**
	 * write
	 * Writes an object
	 */
	public static Entity gleam_write(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity obj = null, obj2;
		ListIterator it = new ListIterator(args);
		OutputPort out;

		// get object to print
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("write: too few arguments", args);
		}

		// get output port, if present
		if (it.hasNext()) {
			obj2 = it.next();
			if (obj2 instanceof OutputPort) {
				out = (OutputPort) obj2;
			}
			else {
				throw new GleamException("write: not an output port", args);
			}
		}
		else {
			out = System.getCout();
		}

		// print object
		if (!it.hasNext()) {
			if (out.isOpen()) {
				out.write(obj);
				return Void.makeVoid();
			}
			else {
				throw new  GleamException("write: closed output port", args);
			}
		}
		else {
			throw new GleamException("write: too many arguments", args);
		}
	}

	/**
	 * newline
	 * Writes an end of line
	 */
	public static Entity gleam_newline(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		try {
			OutputPort oport;
			ListIterator it = new ListIterator(args);
			if (it.hasNext()) {
				oport = (OutputPort) it.next();
				if (it.hasNext()) {
					throw new  GleamException("newline: too many arguments", args);
				}
			}
			else {
				oport = gleam.lang.System.getCout();
			}

			if (oport.isOpen()) {
				oport.newline();
				return Void.makeVoid();
			}
			else {
				throw new  GleamException("newline: closed output port", args);
			}
		}
		catch (ClassCastException e) {
			throw new  GleamException("newline: not an output port", args);
		}
	}

}