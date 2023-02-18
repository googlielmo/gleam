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

package gleam.library;

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.MutableString;
import gleam.lang.Number;
import gleam.lang.OutputPort;
import gleam.lang.Real;
import gleam.lang.Symbol;
import gleam.lang.Void;
import gleam.util.Logger;
import gleam.util.Logger.Level;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.Set;

import static gleam.lang.Environment.Kind.INTERACTION_ENV;

/**
 * INTERACTION (GLEAM-SPECIFIC)
 * <p>
 * Primitive operator and procedure implementation library.
 */
public final class Interaction
{

    public static final String INVALID_ARGUMENT = "invalid argument";
    private static final Logger logger = Logger.getLogger();
    /**
     * This array contains definitions of primitives. It is used by static
     * initializers in gleam.lang.System to populate the initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * help
             * Gives help on primitives.
             */
            new Primitive("help",
                          INTERACTION_ENV,
                          Primitive.KEYWORD, /* environment, type */
                          0,
                          1, /* min, max no. of arguments */
                          "Gives a short help on a primitive, e.g. (help if)",
                          null /* doc strings */)
            {
                private static final int HELP_COLUMN_WIDTH = 19;

                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    OutputPort cout = env.getExecutionContext().getOut();
                    if (arg1 != null) {
                        // we have an explicit argument,
                        // so print full documentation
                        if (!(arg1 instanceof Symbol)) {
                            throw new GleamException(this,
                                                     INVALID_ARGUMENT,
                                                     arg1);
                        }

                        String pname = arg1.toString();
                        String doc = Interpreter.getHelpDocumentation(pname);
                        if (doc != null) {
                            cout.print(doc);
                        }
                        else {
                            cout.print("No documentation available for ");
                            cout.print(arg1.toString());
                            cout.print(". Try (help).");
                        }
                        cout.newline();
                        return Void.VALUE;
                    }

                    // no args: print short comments on all primitives
                    // prepare filler for first column
                    char[] chars = new char[HELP_COLUMN_WIDTH];
                    java.util.Arrays.fill(chars, ' ');
                    StringBuilder spc = new StringBuilder();
                    spc.append(chars);

                    cout.print("Available primitives:\n\n");
                    Set<String> nameset = Interpreter.getHelpNames();
                    for (String s : nameset) {
                        StringBuilder pname = new StringBuilder(s);
                        String doc = Interpreter.getHelpComment(pname.toString());
                        if (doc != null) {
                            if (pname.length() < HELP_COLUMN_WIDTH) {
                                pname.append(spc.subSequence(0,
                                                             HELP_COLUMN_WIDTH - pname.length()));
                            }
                            cout.print(pname.toString());
                            cout.print(" ");
                            cout.print(doc);
                        }
                        else {
                            cout.print("No documentation available ");
                            cout.print("(but it should be!). ");
                            cout.print("Please report to Gleam developers.");
                        }
                        cout.newline();
                    }
                    cout.newline();
                    cout.print(
                            "Special variable __errobj contains last offending object after an error.");
                    cout.newline();
                    return Void.VALUE;
                }
            },

            /*
             * set-verbosity!
             * Sets gleam runtime support verbosity (1..5)
             */
            new Primitive("set-verbosity!",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Sets verbosity level: 0=off, 1=standard ... 5=pedantic",
                          "E.g. (set-verbosity! 2)" /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    if (!(arg1 instanceof Number)) {
                        throw new GleamException(this, INVALID_ARGUMENT, arg1);
                    }
                    double v = ((Number) arg1).doubleValue();
                    if (v < Level.ALL.getValue() || v > Level.ERROR.getValue()) {
                        throw new GleamException(this,
                                                 "invalid argument (should be between " + Level.ALL + " and " + Level.ERROR + ")",
                                                 arg1);
                    }
                    Logger.getLogger().setLevel(Level.OFF.getValue() - (int) v);
                    return Void.VALUE;
                }
            },

            /*
             * verbosity
             * Gets gleam runtime support verbosity (1..5)
             */
            new Primitive("verbosity",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          0,
                          0, /* min, max no. of arguments */
                          "Returns current verbosity level",
                          null /* doc strings */)
            {
                @Override
                public Entity apply0(Environment env, Continuation cont)
                {
                    return new Real(Level.OFF.getValue() - logger.getLevelValue());
                }
            },

            /*
             * save-session
             * Saves the session environment.
             */
            new Primitive("save-session",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Saves current session environment, e.g. (save-session \"file\")",
                          null /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    if (arg1 instanceof MutableString) {
                        try (FileOutputStream fos = new java.io.FileOutputStream(
                                arg1.toString());
                             ObjectOutput output = new java.io.ObjectOutputStream(
                                     fos)) {
                            output.writeObject(env.getInterpreter()
                                                  .getSessionEnv());
                            return Void.VALUE;
                        }
                        catch (java.io.FileNotFoundException e) {
                            throw new GleamException(this,
                                                     "file not found",
                                                     arg1);
                        }
                        catch (java.io.IOException e) {
                            Logger.getLogger().warning(e);
                            throw new GleamException(this, "I/O warning", arg1);
                        }
                    }
                    else {
                        throw new GleamException(this, INVALID_ARGUMENT, arg1);
                    }
                }
            },

            /*
             * load-session
             * Loads the session environment.
             */
            new Primitive("load-session",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Loads a session environment, e.g. (load-session \"file\")",
                          null /* doc strings */)
            {
                @Override
                public Entity apply1(Entity arg1,
                                     Environment env,
                                     Continuation cont) throws GleamException
                {
                    if (arg1 instanceof MutableString) {
                        try (FileInputStream fis = new FileInputStream(arg1.toString());
                             ObjectInput input = new ObjectInputStream(fis)) {
                            Environment newEnv = (Environment) input.readObject();

                            env.getInterpreter().setSessionEnv(newEnv);
                            return Void.VALUE;
                        }
                        catch (java.io.FileNotFoundException e) {
                            logger.warning(e);
                            throw new GleamException(this,
                                                     "file not found",
                                                     arg1);
                        }
                        catch (java.io.IOException e) {
                            logger.warning(e);
                            throw new GleamException(this, "I/O warning", arg1);
                        }
                        catch (ClassNotFoundException e) {
                            logger.warning(e);
                            throw new GleamException(this,
                                                     "class not found",
                                                     arg1);
                        }
                        catch (ClassCastException e) {
                            logger.warning(e);
                            throw new GleamException(this,
                                                     "invalid class",
                                                     arg1);
                        }
                    }
                    else {
                        throw new GleamException(this, INVALID_ARGUMENT, arg1);
                    }
                }
            }

    }; // primitives

    /** Can't instantiate this class. */
    private Interaction() {}
}
