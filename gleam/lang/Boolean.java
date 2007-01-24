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
 * Boolean.java
 *
 * Created on October 26, 2001, 9:27 PM
 */

/**
 * Scheme boolean.
 */
public final class Boolean extends Entity
{
	/** the truth value of this object */
	protected boolean value;
	
	/** the one and only #t */
	static public final Boolean trueValue = new Boolean(true);
	
	/** the one and only #f */
	static public final Boolean falseValue = new Boolean(false);

	/** private constructor */
	private Boolean(boolean v)
	{
		value = v;
	}

	/**
	 * Static factory.
	 */
	public static Boolean makeBoolean(boolean b)
	{
		if (b)
			return trueValue;
		else
			return falseValue;
	}

	/**
	 * Writes a boolean.
	 */
	public void write(java.io.PrintWriter out)
	{
		if (value) {
			out.print("#t");
		}
		else {
			out.print("#f");
		}	
	}
}
