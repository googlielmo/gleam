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
import gleam.lang.Environment;
import gleam.lang.Eof;
import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.OutputPort;
import gleam.lang.Pair;
import gleam.lang.Symbol;
import gleam.lang.Void;
import gleam.util.Logger;

/**
 * The Gleam interactive interpreter.
 */
public class Gleam {

    // Gleam release number
    static final String RELEASE = "1.2-SNAPSHOT";

    // the interactive prompt
    static final String PROMPT = "> ";
    // Quit control symbol
    static final Symbol C_QUIT = Symbol.makeSymbol("!q");
    // Dump env control symbol
    static final String C_ENV = "!e";
    // Help control symbol
    static final String C_HELP = "!h";
    // Trace on control symbol
    static final String C_TRON = "!tron";
    // Trace off control symbol
    static final String C_TROFF = "!troff";
    // Shortcut for '(help)
    static final Entity CALL_HELP = new Pair(Symbol.HELP, EmptyList.value());

    /**
     * Entry point for the Gleam interactive interpreter
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        final Interpreter intp = bootstrap();
        final Environment session = intp.getSessionEnv();
        final OutputPort w = intp.getCout();

        welcome(w);

        Entity obj = Void.value();

        // the REPL loop
        do {
            try {
                prompt(w, PROMPT);

                if ((obj = readEntity(intp)) != null) {
                    Entity result = intp.eval(obj, session);

                    if (result != Void.value()) {
                        w.write(result);
                    }
                    w.newline();
                }
            } catch (GleamException e) {
                intp.getInteractionEnv().define(Symbol.ERROBJ, e.value());
                w.printf("*** %s\n", e.getMessage());
                intp.clearContinuation();
            } catch (Exception e) {
                w.printf("*** Uncaught Exception: %s\n", e.getMessage());
                Logger.error(e);
                intp.clearContinuation();
            }
        } while (obj != null);

        prompt(w, "Bye!");
    }

    private static void prompt(OutputPort w, String prompt)
    {
        if (w.isConsole()) {
            w.print(prompt);
            w.flush();
        }
    }

    private static Interpreter bootstrap()
    {
        final Interpreter intp;
        try {
            Logger.enter(Logger.Level.FINE, "Bootstrapping Gleam... ");
            intp = Interpreter.newInterpreter();
            Logger.enter(Logger.Level.FINE, "... done");
        } catch (GleamException e) {
            Logger.error(e);
            System.exit(1);
            return null;
        }
        return intp;
    }

    private static void welcome(OutputPort w)
    {
        if (w.isConsole()) {
            String version = Gleam.class.getPackage().getImplementationVersion();
            w.printf("Welcome to Gleam, release %s\n", version != null ? version : RELEASE);
            w.print("(c) 2001-2020 Guglielmo Nigri <guglielmonigri@yahoo.it>.\n");
            w.print("Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are\n");
            w.print("welcome to redistribute it under certain conditions; see LICENSE.TXT.\n");
            w.print("Type !h for help, !q to quit.\n\n");
        }
    }

    private static Entity readEntity(Interpreter intp) throws GleamException
    {
        Entity obj = intp.getCin().read();

        // check for EOF
        if (obj == Eof.value() || obj == C_QUIT) {
            return null;
        }

        // check for control symbols
        if (obj instanceof Symbol) {
            switch (obj.toString()) {
                case C_ENV:
                    intp.getSessionEnv().dump();
                    obj = Void.value();
                    break;
                case C_TRON:
                    Interpreter.traceOn();
                    obj = Void.value();
                    break;
                case C_TROFF:
                    Interpreter.traceOff();
                    obj = Void.value();
                    break;
                case C_HELP:
                    intp.getCout().print("Enable trace with !tron, disable with !troff.\n\n");
                    obj = CALL_HELP;
                    break;
                default:
            }
        }

        return obj;
    }
}
