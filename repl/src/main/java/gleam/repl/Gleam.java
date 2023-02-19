/*
 * Copyright (c) 2001-2023 Guglielmo Nigri.  All Rights Reserved.
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

package gleam.repl;

import gleam.GleamScriptEngine;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static gleam.util.Logger.Level.WARNING;

/**
 * The Gleam interactive interpreter.
 */
public class Gleam
{

    // the interactive prompt
    static final String PROMPT = "> ";
    // Quit control symbol
    static final String C_QUIT = "!q";
    // Dump env control symbol
    static final String C_ENV = "!e";
    // Help control symbol
    static final String C_HELP = "!h";
    // Trace on control symbol
    static final String C_TRON = "!tron";
    // Trace off control symbol
    static final String C_TROFF = "!troff";
    // Shortcut for '(help)
    static final Entity CALL_HELP = new Pair(Symbol.HELP, EmptyList.VALUE);

    private static final Logger logger = Logger.getLogger();

    private String release;

    private boolean console;

    /**
     * Entry point for the Gleam interactive interpreter
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        new Gleam().repl(args);
        System.exit(0);
    }

    private void repl(String[] args)
    {

        logger.setLevel(WARNING);

        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("gleam");
        final Interpreter intp = ((GleamScriptEngine) engine).getInterpreter();
        final Environment session = intp.getSessionEnv();
        final OutputPort w = session.getExecutionContext().getOut();

        if (args.length == 1 && args[0].equals("--force-console")) {
            console = true;
        }
        else {
            console = w.isConsole();
        }

        release = engine.getFactory().getEngineVersion();

        welcome(w);

        Entity obj = Void.VALUE;

        // the REPL loop
        do {
            try {
                prompt(w, PROMPT);

                if ((obj = readEntity(session)) != null) {
                    Entity result = intp.eval(obj, session);

                    if (result != Void.VALUE) {
                        w.write(result);
                    }
                    w.newline();
                }
            }
            catch (GleamException e) {
                Interpreter.getInteractionEnv()
                           .define(Symbol.ERROBJ, e.value());
                w.printf("*** %s\n", e.getMessage());
                intp.clearContinuation();
            }
            catch (Exception e) {
                w.printf("*** Uncaught Exception: %s\n", e.getMessage());
                logger.warning(e);
                intp.clearContinuation();
            }
        } while (obj != null);

        prompt(w, "Bye!");
    }

    private void welcome(OutputPort w)
    {
        if (console) {
            w.printf("Welcome to Gleam, release %s\n", release);
            w.print("(c) 2001-2022 Guglielmo Nigri <guglielmonigri@yahoo.it>.\n");
            w.print("Gleam comes with ABSOLUTELY NO WARRANTY.  This is free software, and you are\n");
            w.print("welcome to redistribute it under certain conditions; see LICENSE.TXT.\n");
            w.print("\nType !h for help, !q to quit.\n");
            w.print("Enable trace with !tron, disable with !troff.\n\n");
        }
    }

    private void prompt(OutputPort w, String prompt)
    {
        if (console) {
            w.print(prompt);
            w.flush();
        }
    }

    private Entity readEntity(Environment env) throws GleamException
    {
        Entity obj = env.getExecutionContext().getIn().read();

        // check for EOF
        if (obj == Eof.VALUE) {
            return null;
        }

        // check for control symbols
        if (obj instanceof Symbol) {
            switch (obj.toString()) {
                case C_QUIT:
                    obj = null;
                    break;
                case C_ENV:
                    env.dump();
                    obj = Void.VALUE;
                    break;
                case C_TRON:
                    env.getExecutionContext().setTraceEnabled(true);
                    obj = Void.VALUE;
                    break;
                case C_TROFF:
                    env.getExecutionContext().setTraceEnabled(false);
                    obj = Void.VALUE;
                    break;
                case C_HELP:
                    obj = CALL_HELP;
                    break;
                default:
            }
        }

        return obj;
    }
}
