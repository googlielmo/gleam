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
	protected int minArgs, maxArgs;
	
	protected static final Class[][] param = new Class[][] {
		new Class[] { Environment.class, Continuation.class },
		new Class[] { Entity.class, Environment.class, Continuation.class },
		new Class[] { Entity.class, Entity.class, Environment.class, Continuation.class },
		new Class[] { Entity.class, Entity.class, Entity.class, Environment.class, Continuation.class },
		new Class[] { Pair.class, Environment.class, Continuation.class },
	};
	
	protected static final String LIBRARY = "gleam.library.";

	protected String opName, shortName;
	protected transient Method value;

	/**
	 * PrimitiveProcedure
	 */
	protected PrimitiveProcedure() {
	}

	public PrimitiveProcedure(String opName, String shortName, int minArgs, int maxArgs)
		throws GleamException
	{
		String fullName = null;
		try {
			this.minArgs = minArgs;
			this.maxArgs = maxArgs;
			this.opName = opName;
			this.shortName = shortName;
			
			fullName = LIBRARY + shortName + argSuffix();
			String classname = fullName.substring(0, fullName.lastIndexOf('.'));
			String methodname = fullName.substring(fullName.lastIndexOf('.')+1);
			Class primitiveClass = Class.forName(classname);
			
			this.value = primitiveClass.getMethod(methodname, getParam());
		}
		catch (java.lang.ClassNotFoundException e) {
			throw new GleamException(
				"PrimitiveProcedure.<init> ("
				+ fullName
				+ "): ClassNotFoundException: "
				+ e.getMessage(), null);
		}
		catch (java.lang.NoSuchMethodException e) {
			throw new GleamException(
				"PrimitiveProcedure.<init> ("
				+ fullName
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
			return new PrimitiveProcedure(opName, shortName, minArgs, maxArgs);
		}
		catch (gleam.lang.GleamException e) {
			throw new java.io.InvalidObjectException("No such method");
		}
	}

	public Entity apply(Pair arg, Environment env, Continuation cont)
		throws GleamException
	{
		try {
			if (maxArgs < 0 || maxArgs > 3) {
				if (minArgs >= 0 && maxArgs >= 0) {
					checkNumArgs(arg);
				}
				return (Entity) value.invoke(null, new java.lang.Object[] {
					arg, env, cont} );
			}
			// ok, 1 <= maxArgs <= 3 : special rules
			assert 1 <= maxArgs && maxArgs <= 3; // DEBUG
			Entity[] argArray = new Entity[] {null, null, null};
			int countedArgs = 0;
			ListIterator it = new ListIterator(arg);
			while (it.hasNext()) {
				argArray[countedArgs++] = it.next();
				if (countedArgs > maxArgs) {
					throw new GleamException(opName + ": too many arguments", arg);
				}
			}
			if (countedArgs < minArgs) {
				throw new GleamException(opName + ": too few arguments", arg);
			}
			switch (maxArgs) {
				case 1:
					return (Entity) value.invoke(null, new java.lang.Object[] {
						argArray[0], env, cont} );
				case 2:
					return (Entity) value.invoke(null, new java.lang.Object[] {
						argArray[0], argArray[1], env, cont} );
				case 3:
					return (Entity) value.invoke(null, new java.lang.Object[] {
						argArray[0], argArray[1], argArray[2], env, cont} );
				default: // DEBUG CANNOT HAPPEN
					assert false;
					return null;
			}
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

	private String argSuffix() {
		if (minArgs == maxArgs) 
			return "_$" + minArgs;
		else if (maxArgs == -1) {
			if (minArgs == 0)
				return "";
			else
				return "_$" + minArgs + "_N";
		}
		else
			return "_$" + minArgs + "_" + maxArgs;
	}

	private Class[] getParam() {
			if (maxArgs < 0 || maxArgs > 3)
				return param[4];
			else
				return param[maxArgs];
	}

	private void checkNumArgs(Pair args) throws GleamException {
		ListIterator it = new ListIterator(args);
		int i;
		for (i = 0; i < minArgs; ++i) {
			if (!it.hasNext()) {
				throw new GleamException(opName + ": too few arguments", args);
			}
		}
		if (maxArgs > 0) {
			while (it.hasNext()) {
				if (++i > maxArgs)
					throw new GleamException(opName + ": too many arguments", args);
			}
		}
	}

}
