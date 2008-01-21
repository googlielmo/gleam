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

/*
 * EmptyList.java
 *
 * Created on October 24, 2001, 1:55 AM
 */

/**
 * This class represents the distinct type of the Scheme empty list.
 * There is only one instance of this class, i.e. the empty list itself '().
 * EmptyList is a specialization of Pair uniquely for efficiency reasons in
 * procedure evaluation.
 */
public final class EmptyList extends Pair {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** the EmptyList singleton */
	static final EmptyList value = new EmptyList();

	/** Can't create an empty list */
	private EmptyList() {
		super(null, null);
		analyzed = true;
	}

	/**
	 * factory method
	 */
	public static EmptyList makeEmptyList() {
		return value;
	}

	/**
	 * Evaluates the empty list, thus resulting in an error.
	 * The empty combination is an error in Scheme, see r5rs.
	 */
	public Entity eval(Environment env, Continuation cont)
		throws GleamException
	{
		throw new GleamException("invalid combination: empty list", this);
	}

	/**
	 * Prevents the release of multiple instances upon deserialization.
	 */
	protected java.lang.Object readResolve()
		throws java.io.ObjectStreamException
	{
//		java.lang.System.out.println("readResolve() called! (EmptyList)"); //DEBUG
		return value;
	}

	/**
	 * Writes the empty list value.
	 */
	public void write(java.io.PrintWriter out)
	{
		out.print("()");
	}

	/**
	 * Performs environment optimization on this pair.
	 */
	public Entity optimize(Environment env) throws GleamException {
		// no optimization!
		return this;
	}

	public Entity getCar() throws GleamException {
		throw new GleamException("car: invalid arguments", this);
	}

	public Entity getCdr() throws GleamException {
		throw new GleamException("cdr: invalid arguments", this);
	}

	public void setCar(Entity obj) throws GleamException {
		throw new GleamException("set-car!: invalid arguments", this);
	}

	public void setCdr(Entity obj) throws GleamException {
		throw new GleamException("set-cdr!: invalid arguments", this);
	}


}
