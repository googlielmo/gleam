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
 * Ports
 * Primitive operator and procedure implementation library.
 */
public final class Ports {

	/**
	 * Can't instantiate this class
	 */
	private Ports() {
	}

	/**
	 * port?
	 * Tests if argument is a port
	 */
	public static Entity gleam_port_p(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity obj = null;
		ListIterator it = new ListIterator(args);	
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("port?: too few arguments", args);
		}

		if (!it.hasNext()) {
			return Boolean.makeBoolean(obj instanceof Port);
		}
		else {
			throw new GleamException("port?: too many arguments", args);
		}
	}

	/**
	 * input-port?
	 * Tests if argument is an input port
	 */
	public static Entity gleam_input_port_p(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity obj = null;
		ListIterator it = new ListIterator(args);	
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("input-port?: too few arguments", args);
		}

		if (!it.hasNext()) {
			return Boolean.makeBoolean(obj instanceof InputPort);
		}
		else {
			throw new GleamException("input-port?: too many arguments", args);
		}
	}

	/**
	 * output-port?
	 * Tests if argument is an output port
	 */
	public static Entity gleam_output_port_p(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		Entity obj = null;
		ListIterator it = new ListIterator(args);	
		if (it.hasNext()) {
			obj = it.next();
		}
		else {
			throw new GleamException("output-port?: too few arguments", args);
		}

		if (!it.hasNext()) {
			return Boolean.makeBoolean(obj instanceof OutputPort);
		}
		else {
			throw new GleamException("port?: too many arguments", args);
		}
	}

	/**
	 * current-input-port
	 * Returns the current input port
	 */
	public static Entity gleam_current_input_port(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		if (args == EmptyList.makeEmptyList()) {
			return gleam.lang.System.getCin();
		}
		else {
			throw new GleamException("current-input-port: too many arguments", args);
		}
	}

	/**
	 * current-output-port
	 * Returns the current output port
	 */
	public static Entity gleam_current_output_port(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		if (args == EmptyList.makeEmptyList()) {
			return gleam.lang.System.getCout();
		}
		else {
			throw new GleamException("current-output-port: too many arguments", args);
		}
	}

}