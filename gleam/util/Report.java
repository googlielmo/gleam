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

package gleam.util;

/*
 * Report.java
 *
 * Created on October 18, 2001, 1:02 AM
 */

/**
 * Reporting utility class for Gleam.
 */
public class Report
{
	/**
	 * Current level of verbosity
	 * 5 = internal debugging information
	 * 4 = information of interest to implementers (Java stack traces)
	 * 3 = unusual or remarkable activities
	 * 2 = user warnings
	 * 1 = user errors (default)
	 * 0 = don't print anything from Gleam
	 */
	protected static int verbosity = 1;

	/** Can't instantiate this one */
	private Report()
	{
	}

	/**
	 * Gets current verbosity.
	 * @return int
	 */
	public static int getVerbosity() {
		return verbosity;
	}

	/**
	 * Prints a message from Gleam internals, respecting current verbosity.
	 */
	public static void println(int severity, String message)
	{
		print(severity, message);
		println(severity);
	}

	/**
	 * Prints an object from Gleam internals, respecting current verbosity.
	 */
	public static void println(int severity, gleam.lang.Entity obj)
	{
		print(severity, obj);
		println(severity);
	}

	/**
	 * Prints a message and an object from Gleam internals, respecting current verbosity.
	 */
	public static void println(int severity, String message, gleam.lang.Entity obj)
	{
		print(severity, message, obj);
		println(severity);
	}

	/**
	 * Prints an end-of-line from Gleam internals, respecting current verbosity.
	 */
	public static void println(int severity)
	{
		if (severity <= verbosity)
		{
			gleam.lang.System.getCout().newline();
		}
	}

	/**
	 * Prints a message from Gleam internals, respecting current verbosity.
	 */
	public static void print(int severity, String message)
	{
		if (severity <= verbosity)
		{
			gleam.lang.System.getCout().print("["+ severity +"] "+ message);
		}
	}

	/**
	 * Prints an object from Gleam internals, respecting current verbosity.
	 */
	public static void print(int severity, gleam.lang.Entity obj)
	{
		if (severity <= verbosity)
		{
			gleam.lang.System.getCout().print("[" + severity + "] ");
			gleam.lang.System.getCout().write(obj);
		}
	}

	/**
	 * Prints a message and an object from Gleam internals, respecting current verbosity.
	 */
	public static void print(int severity, String message, gleam.lang.Entity obj)
	{
		if (severity <= verbosity)
		{
			gleam.lang.System.getCout().print("[" + severity + "] " + message + " ");
			gleam.lang.System.getCout().write(obj);
		}
	}

	/**
	 * Sets verbosity level.
	 * @param newVerbosity int
	 */
	public static void setVerbosity(int newVerbosity) {
		verbosity = newVerbosity;
	}

	public static void printStackTrace(Exception ex) {
		if (verbosity >= 4) {
			ex.printStackTrace(gleam.lang.System.getCout().getPrintWriter());
		}
	}
}
