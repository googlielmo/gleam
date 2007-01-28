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

import gleam.util.Report;
import java.lang.reflect.*;

/**
 * Scheme primitive library procedure.
 */
public class PrimitiveProcedure extends Procedure
{
	protected static final Class[] param =
		new Class[] { Pair.class, Environment.class, Continuation.class };
	protected static final String LIBRARY = "gleam.library.";

	protected String name;
	protected transient Method value;

	/**
	 * PrimitiveProcedure
	 */
	protected PrimitiveProcedure() {
	}

	public PrimitiveProcedure(String shortname)
		throws GleamException
	{
		try {
			name = LIBRARY + shortname;

			String classname =
				name.substring(0, name.lastIndexOf('.'));
			String methodname =
				name.substring(name.lastIndexOf('.')+1);
			Class primitiveClass = Class.forName(classname);

			value = primitiveClass.getMethod(methodname, param);
		}
		catch (java.lang.ClassNotFoundException e) {
			throw new GleamException(
				"CompiledProcedure c'tor ("
				+ name
				+ "): ClassNotFoundException: "
				+ e.getMessage(), null);
		}
		catch (java.lang.NoSuchMethodException e) {
			throw new GleamException(
				"CompiledProcedure c'tor ("
				+ name
				+ "): NoSuchMethodException: "
				+ e.getMessage(), null);
		}
	}

	/**
	 * Prevents the release of incorrect instances upon deserialization.
	 */
	protected java.lang.Object readResolve()
		throws java.io.ObjectStreamException
	{
//		java.lang.System.out.println("readResolve() called! (PrimitiveProcedure)"); //DEBUG
		try {
			return new PrimitiveProcedure(
				name.substring(LIBRARY.length()));
		}
		catch (gleam.lang.GleamException e) {
			throw new java.io.InvalidObjectException("No such method");
		}
	}

	public Entity apply(Pair arg, Environment env, Continuation cont)
		throws GleamException
	{
		try {
			return (Entity) value.invoke(null,
					new java.lang.Object[] {arg, env, cont} );
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof GleamException) {
				throw (GleamException)t;
			}
			else {
				Report.printStackTrace(e);
				throw new GleamException("apply: InvocationTargetException: " + e.getMessage(), this);
			}
		}
		catch (java.lang.IllegalAccessException e) {
			throw new GleamException("apply: IllegalAccessException: " + e.getMessage(), this);
		}
		catch (java.lang.ClassCastException e) {
			throw new GleamException("apply: ClassCastException: " + e.getMessage(), this);
		}
	}

	public void write(java.io.PrintWriter out)
	{
		out.write("#<primitive-procedure "+ value.getName() + ">");
	}

}

