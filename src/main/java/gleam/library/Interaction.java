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
import gleam.lang.System;
import gleam.lang.Void;
import gleam.util.Log;
import gleam.util.Log.Level;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.Set;

/**
 * INTERACTION -- GLEAM-SPECIFIC
 * Primitive operator and procedure implementation library.
 */
public final class Interaction {

    /**
     * Can't instantiate this class
     */
    @SuppressWarnings("unused")
    private Interaction() {
    }

    /**
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the initial environments.
     */
    public static final Primitive[] primitives = {

    /*
     * help
     * Gives help on primitives.
     */
    new Primitive( "help",
        Primitive.INTR_ENV, Primitive.KEYWORD, /* environment, type */
        0, 1, /* min, max no. of arguments */
        "Gives a short help on a primitive, e.g. (help if)",
        null /* doc strings */ ) {
    private static final int helpColumnWidth = 15;
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        OutputPort cout = Interpreter.getInterpreter().getCout();
        if (arg1 != null) {
            // we have an explicit argument,
            // so print full documentation
            if (!(arg1 instanceof Symbol)) {
                throw new GleamException(this, "invalid argument", arg1);
            }

            String pname = arg1.toString();
            String doc = System.getHelpDocumentation(pname);
            if (doc != null) {
                cout.print(doc);
            }
            else {
                cout.print("No documentation available for ");
                cout.print(arg1.toString());
                cout.print(". Try (help).");
            }
            cout.newline();
            return Void.value();
        }

        // no args: print short comments on all primitives
        // prepare filler for first column
        char[] chars = new char[helpColumnWidth];
        java.util.Arrays.fill(chars, ' ');
        StringBuilder spc = new StringBuilder();
        spc.append(chars);

        cout.print("Available primitives:\n\n");
        Set<String> nameset = System.getHelpNames();
        for (String s : nameset) {
            StringBuilder pname = new StringBuilder(s);
            String doc = System.getHelpComment(pname.toString());
            if (doc != null) {
                if (pname.length() < helpColumnWidth) {
                    pname.append(spc.subSequence(0, helpColumnWidth - pname.length()));
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
        cout.print("Special variable __errobj contains last offending object after an error.");
        cout.newline();
        return Void.value();
    }},

    /*
     * set-verbosity!
     * Sets gleam runtime support verbosity (1..5)
     */
    new Primitive( "set-verbosity!",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Sets verbosity level: 0=off, 1=standard ... 5=pedantic",
        "E.g. (set-verbosity! 2)" /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        if (!(arg1 instanceof Number)) {
            throw new GleamException(this, "invalid argument", arg1);
        }
        double v = ((Number)arg1).getDoubleValue();
        if (v < Level.ALL.getValue() || v > Level.ERROR.getValue()) {
            throw new GleamException(this,
                    "invalid argument (should be between "+ Level.ALL +" and " + Level.ERROR +")",
                    arg1);
        }
        Log.setLevel(Level.OFF.getValue() - (int) v);
        return Void.value();
    }},

    /*
     * verbosity
     * Gets gleam runtime support verbosity (1..5)
     */
    new Primitive( "verbosity",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, 0, /* min, max no. of arguments */
        "Returns current verbosity level", null /* doc strings */ ) {
    @Override
    public Entity apply0(Environment env, Continuation cont)
    {
        return new Real(Level.OFF.getValue() - Log.getLevelValue());
    }},

    /*
     * save-session
     * Saves the session environment.
     */
    new Primitive( "save-session",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Saves current session environment, e.g. (save-session \"file\")",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        if (arg1 instanceof MutableString) {
            try (FileOutputStream fos = new java.io.FileOutputStream(arg1.toString());
                 ObjectOutput output = new java.io.ObjectOutputStream(fos))
            {
                output.writeObject(Interpreter.getInterpreter().getSessionEnv());
                return Void.value();
            }
            catch (java.io.FileNotFoundException e) {
                throw new GleamException(this, "file not found", arg1);
            }
            catch (java.io.IOException e) {
                Log.error(e);
                throw new GleamException(this, "I/O error", arg1);
            }
        }
        else {
            throw new GleamException(this, "invalid argument", arg1);
        }
    }},

    /*
     * load-session
     * Loads the session environment.
     */
    new Primitive( "load-session",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Loads a session environment, e.g. (load-session \"file\")",
        null /* doc strings */ ) {
    @Override
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        if (arg1 instanceof MutableString) {
            try (FileInputStream fis = new FileInputStream(arg1.toString());
                 ObjectInput input = new ObjectInputStream(fis))
            {
                Environment newEnv = (Environment) input.readObject();
                Interpreter.getInterpreter().setSessionEnv(newEnv);
                return Void.value();
            }
            catch (java.io.FileNotFoundException e) {
                Log.error(e);
                throw new GleamException(this, "file not found", arg1);
            }
            catch (java.io.IOException e) {
                Log.error(e);
                throw new GleamException(this, "I/O error", arg1);
            }
            catch (ClassNotFoundException e) {
                Log.error(e);
                throw new GleamException(this, "class not found", arg1);
            }
            catch (ClassCastException e) {
                Log.error(e);
                throw new GleamException(this, "invalid class", arg1);
            }
        }
        else {
            throw new GleamException(this, "invalid argument", arg1);
        }
    }},

    }; // primitives
}
