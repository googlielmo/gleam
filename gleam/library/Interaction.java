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
import gleam.util.Report;

/**
 * INTERACTION -- GLEAM-SPECIFIC
 * Primitive operator and procedure implementation library.
 */
public final class Interaction {

	/**
	 * Can't instantiate this class
	 */
	private Interaction() {
	}

	/**
	 * Help
	 * Gives help on primitives.
	 */
	public static Entity gleam_help_$0_1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		if (arg1 != null) {
			// we have an explicit argument,
			// so print full documentation
			if (!(arg1 instanceof Symbol)) {
				throw new GleamException("help: invalid argument", arg1);
			}

			String pname = ((Symbol)arg1).toString();
			String doc = System.getHelpDocumentation(pname);
			if (doc != null) {
				System.getCout().print(doc);
				System.getCout().newline();
			}
			else {
				System.getCout().print("No documentation available for ");
				System.getCout().print(((Symbol)arg1).toString());
				System.getCout().print(". Try (help).");
				System.getCout().newline();
			}
			return Void.makeVoid();
		}

		// no args: print short comments on all primitives
		System.getCout().print("Available primitives:\n\n");
		java.util.Set nameset = System.getHelpNames();
		java.util.Iterator nameit = nameset.iterator();
		while (nameit.hasNext()) {
			String pname = (String) nameit.next();
			String doc = System.getHelpComment(pname);
			if (doc != null) {
				if (pname.length() < 16) {
					pname = (pname
						+ "                ") // 16 spaces
						.substring(0,15);
				}
				System.getCout().print(pname);
				System.getCout().print(" ");
				System.getCout().print(doc);
				System.getCout().newline();
			}
			else {
				System.getCout().print("No documentation available ");
				System.getCout().print("(but it should be!). ");
				System.getCout().print("Please report to Gleam team.");
				System.getCout().newline();
			}
		}
		System.getCout().newline();
		System.getCout().print("Special variable __errobj contains last offending object after an error.");
		System.getCout().newline();
		return Void.makeVoid();
	}

	/**
	 * Verbosity
	 * Sets gleam runtime support verbosity (1..5)
	 */
	public static Entity gleam_verbosity_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		if (!(arg1 instanceof Number)) {
			throw new GleamException("verbosity: invalid argument", arg1);
		}
		double v = ((Number)arg1).getDoubleValue();
		if (v < 0.0 || v > 5.0) {
			throw new GleamException("verbosity: invalid argument (should be between 0 and 5)", arg1);
		}
		gleam.util.Report.setVerbosity((int) v);
		return Void.makeVoid();
	}

	/**
	 * Save-session
	 * Saves the session environment.
	 */
	public static Entity gleam_save_session_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		if (arg1 instanceof MutableString) {
			try {
				java.io.FileOutputStream
					f = new java.io.FileOutputStream(((MutableString)arg1).toString());
				java.io.ObjectOutput
					s = new java.io.ObjectOutputStream(f);
				s.writeObject(env.getInterpreter().getSessionEnv());
				return Void.makeVoid();
			}
			catch (java.io.FileNotFoundException e) {
				throw new GleamException("save-session: file not found", arg1);
			}
			catch (java.io.IOException e) {
				Report.printStackTrace(e);
				throw new GleamException("save-session: I/O error", arg1);
			}
		}
		else {
			throw new GleamException("save-session: invalid argument", arg1);
		}
	}

	/**
	 * Load-session
	 * Loads the session environment.
	 */
	public static Entity gleam_load_session_$1(Entity arg1, Environment env, Continuation cont)
		throws GleamException
	{
		if (arg1 instanceof MutableString) {
			try {
				java.io.FileInputStream
					f = new java.io.FileInputStream(((MutableString)arg1).toString());
				java.io.ObjectInputStream
					s = new java.io.ObjectInputStream(f);
				Environment glob = (Environment) s.readObject();
				env.getInterpreter().setSessionEnv(glob);
				return Void.makeVoid();
			}
			catch (java.io.FileNotFoundException e) {
				throw new GleamException("load-session: file not found", arg1);
			}
			catch (java.io.IOException e) {
				Report.printStackTrace(e);;
				throw new GleamException("load-session: I/O error", arg1);
			}
			catch (java.lang.ClassNotFoundException e) {
				throw new GleamException("load-session: class not found", arg1);
			}
			catch (java.lang.ClassCastException e) {
				throw new GleamException("load-session: invalid class", arg1);
			}
		}
		else {
			throw new GleamException("load-session: invalid argument", arg1);
		}
	}

}
