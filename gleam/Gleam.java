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

package gleam;

import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.Symbol;

/**
 * The Gleam interactive interpreter.
 */
public class Gleam
{
	// Gleam release number
	private static final String RELEASE="0.89 pre-1";

	// Dump env symbol (for debugging)
	private static final Symbol cEnv = Symbol.makeSymbol("!e");

	// Quit symbol 
	private static final Symbol cQuit = Symbol.makeSymbol("!q");
	
	/**
	 * Entry point for the Gleam interactive interpreter
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		Interpreter intp = null;

		System.out.print("Welcome to Gleam, release " + RELEASE + ".\n");
		System.out.print("(c) 2001-2007 Guglielmo Nigri <guglielmonigri@yahoo.it>.\n");
		System.out.print("Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are\n");
		System.out.print("welcome to redistribute it under certain conditions; see LICENSE.TXT.\n");

		try {
			System.out.print("Bootstrapping... ");
			intp = new Interpreter();
			System.out.print("OK\n");
		} catch (GleamException e) {
			System.out.println("\n*** " + e.getMessage());
			System.exit(1);
		}
		System.out.print("Type (help) for help, !q to quit.\n\n");

		gleam.util.Report.setVerbosity(1);

		gleam.lang.Environment session;

		gleam.lang.InputPort r = gleam.lang.System.getCin();
		gleam.lang.OutputPort w = gleam.lang.System.getCout();


		gleam.lang.Entity prompt = new gleam.lang.MutableString("> ");
		gleam.lang.Entity result;
		
		for(;;)
		{
			try {
				// get session environment for execution
				session = intp.getSessionEnv();

				// read
				w.display(prompt);
				w.flush();

				gleam.lang.Entity obj = r.read();
				if (obj == gleam.lang.Eof.makeEof() || obj == cQuit) {
					System.out.println("Bye.");
					break;
				}

				if (obj == cEnv) {
					session.dump();
				}
				else {
					// eval
					result = intp.eval(obj, session);
					
					// print
					if (result != gleam.lang.Void.makeVoid())
						w.write(result);
					w.newline();
				}
			}
			catch (gleam.lang.GleamException e) {
				System.out.println("*** " + e.getMessage());
			}
		}
	}
}
