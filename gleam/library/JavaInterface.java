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

/*
 * JavaInterface.java
 *
 * Created on 20 jan 2007, 14.51
 *
 */

package gleam.library;

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.JavaObject;
import gleam.lang.ListIterator;
import gleam.lang.MutableString;
import gleam.lang.Pair;
import gleam.lang.Real;
import gleam.lang.Symbol;
import gleam.util.Report;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * JAVA INTERFACE
 */
public class JavaInterface {
	
	/**
	 * Can't instantiate this class
	 */
	private JavaInterface() {
	}

	/**
	 * new
	 */
	public static Entity gleam_new_$1_N(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		ListIterator it = new ListIterator(args);
		Entity className = it.next();
		if (!(className instanceof Symbol)) {
			throw new GleamException("new: wrong argument type, should be a symbol", className);
		}
		if (!it.hasNext()) {
			return new JavaObject((Symbol) className);
		}
		List argClasses = new ArrayList();
		List argObjects = new ArrayList();
		while (it.hasNext()) {
			Entity arg = it.next();
			argClasses.add(getJavaClass(arg));
			argObjects.add(getJavaObject(arg));
		}
		return new JavaObject((Symbol) className, (Class[])argClasses.toArray(new Class[0]), argObjects.toArray());
	}	

	/**
	 * call
	 */
	public static Entity gleam_call_$2_N(Pair args, Environment env, Continuation cont)
		throws GleamException
	{
		ListIterator it = new ListIterator(args);
		Entity methodName = it.next();
		if (!(methodName instanceof Symbol)) {
			throw new GleamException("call: wrong argument type, should be a symbol", methodName);
		}
		Entity object = it.next();
		if (!(object instanceof JavaObject)) {
			throw new GleamException("call: wrong argument type, should be a Java object", object);
		}
		List argClasses = new ArrayList();
		List argObjects = new ArrayList();
		while (it.hasNext()) {
			Entity arg = it.next();
			argClasses.add(getJavaClass(arg));
			argObjects.add(getJavaObject(arg));
		}
		return call((JavaObject) object, (Symbol) methodName, (Class[])argClasses.toArray(new Class[0]), argObjects.toArray());		
	}

	private static Entity call(JavaObject object, Symbol methodName, Class[] parameterTypes, Object[] arguments) throws GleamException {
		if (object.getObjectValue() == null) {
			throw new GleamException("call: null pointer", methodName);
		}
		Class clazz = object.getObjectValue().getClass();
		try {
			Method method = clazz.getMethod(methodName.toString(), parameterTypes);
			Object retVal = method.invoke(object.getObjectValue(), arguments);
			// TODO FIXME if method is void return Void.value;
			return getEntityFromObject(retVal);
		} catch (SecurityException ex) {
			Report.printStackTrace(ex);
			throw new GleamException("call: SecurityException: "+ex.getMessage(), methodName);
		} catch (IllegalArgumentException ex) {
			Report.printStackTrace(ex);
			throw new GleamException("call: IllegalArgumentException: "+ex.getMessage(), methodName);
		} catch (NoSuchMethodException ex) {
			Report.printStackTrace(ex);
			throw new GleamException("call: NoSuchMethodException: "+ex.getMessage(), methodName);
		} catch (IllegalAccessException ex) {
			Report.printStackTrace(ex);
			throw new GleamException("call: IllegalAccessException: "+ex.getMessage(), methodName);
		} catch (InvocationTargetException ex) {
			Report.printStackTrace(ex);
			throw new GleamException("call: InvocationTargetException: "+ex.getMessage(), methodName);
		}
	}

	private static Class getJavaClass(Entity arg) throws GleamException {
		if (arg instanceof JavaObject) {
			Object o = ((JavaObject) arg).getObjectValue();
			return o == null ? null : o.getClass();
		}
		else if (arg instanceof MutableString)
		{
			return "".getClass();
		}
		else if (arg instanceof Symbol)
		{
			return "".getClass();
		}
		else if (arg instanceof Real)
		{
			return double.class;
		}
		else 
			throw new GleamException("cannot obtain the Java Class for a Gleam entity", arg);
	}

	private static Object getJavaObject(Entity arg) throws GleamException {
		if (arg instanceof JavaObject) {
			return ((JavaObject) arg).getObjectValue();
		}
		else if (arg instanceof MutableString)
		{
			return ((MutableString) arg).toString();
		}
		else if (arg instanceof Symbol)
		{
			return ((Symbol) arg).toString();
		}
		else if (arg instanceof Real)
		{
			return new Double(((Real) arg).getDoubleValue());
		}
		else
			throw new GleamException("cannot obtain the Java Object for a Gleam entity", arg);
	}

	private static Entity getEntityFromObject(Object object) {
		if (object == null) {
			return new JavaObject();
		}
		else if (object instanceof Entity) {
			return (Entity) object;
		}
		else {
			return new JavaObject(object);
		}
	}
}
