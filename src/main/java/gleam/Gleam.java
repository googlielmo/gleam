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

import gleam.lang.EmptyList;
import gleam.lang.Entity;
import gleam.lang.Eof;
import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import gleam.lang.MutableString;
import gleam.lang.OutputPort;
import gleam.lang.Pair;
import gleam.lang.Symbol;
import gleam.lang.Void;
import gleam.util.Logger;

import java.io.PrintStream;


/**
 * The Gleam interactive interpreter.
 */
public class Gleam
{
    // Gleam release number
    private static final String RELEASE="1.1-SNAPSHOT";

    // Dump env symbol (for debugging)
    private static final Entity cEnv = Symbol.makeSymbol("!e");

    // Help symbol
    private static final Entity cHelp = Symbol.makeSymbol("!h");

    // Quit symbol
    private static final Entity cQuit = Symbol.makeSymbol("!q");

    // Trace on symbol
    private static final Entity cTron = Symbol.makeSymbol("!tron");

    // Trace off symbol
    private static final Entity cTroff = Symbol.makeSymbol("!troff");

    // '(help)
    public static final Entity CALL_HELP = new Pair(Symbol.HELP, EmptyList.value());

    /**
     * Entry point for the Gleam interactive interpreter
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        Interpreter intp = null;
        PrintStream out = java.lang.System.out;
        String version = Gleam.class.getPackage().getImplementationVersion();

        out.printf("Welcome to Gleam, release %s\n", version != null ? version : RELEASE);
        out.println("(c) 2001-2020 Guglielmo Nigri <guglielmonigri@yahoo.it>.");
        out.println("Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are");
        out.println("welcome to redistribute it under certain conditions; see LICENSE.TXT.");

        try {
            out.print("Bootstrapping... ");
            intp = Interpreter.getInterpreter();
            out.println("OK");
        } catch (GleamException e) {
            Logger.error(e);
            java.lang.System.exit(1);
        }
        out.print("Type !h for help, !q to quit.\n\n");

        gleam.lang.Environment session;

        InputPort r = intp.getCin();
        OutputPort w = intp.getCout();

        Entity prompt = new MutableString("> ");
        Entity result;

        while (true)
        {
            try {
                // get session environment for execution
                session = intp.getSessionEnv();

                // read
                w.display(prompt);
                w.flush();

                Entity obj = r.read();

                if (obj == cEnv) {
                    session.dump();
                    continue;
                }
                else if (obj == Eof.value() || obj == cQuit) {
                    out.println("Bye.");
                    break;
                }
                else if (obj == cTron) {
                    intp.traceOn();
                    continue;
                }
                else if (obj == cTroff) {
                    intp.traceOff();
                    continue;
                }
                else if (obj == cHelp) {
                    out.print("Enable trace with !tron, disable with !troff.\n\n");
                    obj = CALL_HELP;
                }

                // eval
                result = intp.eval(obj, session);

                // print
                if (result != Void.value()) w.write(result);
                w.newline();
            }
            catch (GleamException e) {
                out.println("*** " + e.getMessage());
                intp.clearContinuation();
            }
            catch (Exception e){
                out.println("*** Uncaught Exception: " + e.getMessage());
                Logger.error(e);
                intp.clearContinuation();
            }
        }
    }
}
