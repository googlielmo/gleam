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
import gleam.util.Log;


/**
 * The Gleam interactive interpreter.
 */
public class Gleam
{
    // Gleam release number
    private static final String RELEASE="1.1-SNAPSHOT";

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

        System.out.println("Welcome to Gleam, release " + RELEASE);
        System.out.println("(c) 2001-2008 Guglielmo Nigri <guglielmonigri@yahoo.it>.");
        System.out.println("Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are");
        System.out.println("welcome to redistribute it under certain conditions; see LICENSE.TXT.");

        try {
            System.out.print("Bootstrapping... ");
            intp = new Interpreter();
            System.out.println("OK");
        } catch (GleamException e) {
            Log.record(e);
            System.exit(1);
        }
        System.out.print("Type (help) for help, !q to quit.\n\n");

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
            catch (Exception e){
                System.out.println("*** Uncaught Exception: " + e.getMessage());
                gleam.util.Log.record(e);
            }
        }
    }
}
