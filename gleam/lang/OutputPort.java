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

/**
 * Scheme output port object.
 */
public class OutputPort extends Port
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private java.io.PrintWriter out;

	/**
	 * creates an output port to a file
	 */
	public OutputPort(String name)
		throws java.io.IOException
	{
		this.out = new java.io.PrintWriter(
				new java.io.BufferedWriter(
					new java.io.FileWriter(name)));
	}

	/**
	 * creates an output port to a java.io.PrintWriter object
	 */
	public OutputPort(java.io.PrintWriter out)
	{
		this.out = out;
	}

	/**
	 * closes this port
	 */
	public void close()
		throws java.io.IOException
	{
		if (out != null) {
			out.close();
		}
		out = null;
	}

	/**
	 * tests whether port is open
	 */
	public boolean isOpen() {
		return null != out;
	}

	/**
	 * prints object in machine-readable form
	 */
	public void write(Entity obj) {
		obj.write(out);
	}

	/**
	 * prints object in human-readable form
	 */
	public void display(Entity obj) {
		if (obj == EmptyList.value) {
			out.print("()");
		}
		else if (obj instanceof Pair) {
			out.print("(");
			obj.display(out);
			out.print(")");
		}
		else {
			obj.display(out);
		}
	}

	/**
	 * prints a newline and flushes buffer
	 */
	public void newline() {
		out.println();
		out.flush();
	}

	/**
	 * flushes buffer
	 */
	public void flush() {
		out.flush();
	}

	/**
	 * print method useful for primitives
	 */
	public void print(String s) {
		out.print(s);
	}

	/**
	 * returns the underlying PrintWriter
	 */
	public java.io.PrintWriter getPrintWriter() {
		return out;
	}

	/**
	 * Writes this port
	 */
	public void write(java.io.PrintWriter out)
	{
		out.print("#<output-port>");
	}

}

