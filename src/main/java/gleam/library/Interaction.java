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

import gleam.lang.Number;
import gleam.lang.System;
import gleam.lang.Void;
import gleam.lang.*;
import gleam.util.Log;

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
     * This array contains definitions of primitives.
     * It is used by static initializers in gleam.lang.System to populate
     * the initial environments.
     */
    public static Primitive[] primitives = {

    /**
     * help
     * Gives help on primitives.
     */
    new Primitive( "help",
        Primitive.INTR_ENV, Primitive.KEYWORD, /* environment, type */
        0, 1, /* min, max no. of arguments */
        "Gives a short help on a primitive, e.g. (help if)",
        null /* doc strings */ ) {
    private static final int helpColumnWidth = 15;
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        if (arg1 != null) {
            // we have an explicit argument,
            // so print full documentation
            if (!(arg1 instanceof Symbol)) {
                throw new GleamException(this, "invalid argument", arg1);
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
        // prepare filler for first column
        char chars[] = new char[helpColumnWidth];
        java.util.Arrays.fill(chars, ' ');
        StringBuffer spc = new StringBuffer();
        spc.append(chars);
        
        System.getCout().print("Available primitives:\n\n");
        java.util.Set nameset = System.getHelpNames();
        java.util.Iterator nameit = nameset.iterator();
        while (nameit.hasNext()) {
            StringBuffer pname = new StringBuffer((String) nameit.next());
            String doc = System.getHelpComment(pname.toString());
            if (doc != null) {
                if (pname.length() < helpColumnWidth ) {
                    pname.append(spc.subSequence(0, helpColumnWidth - pname.length()));
                }
                System.getCout().print(pname.toString());
                System.getCout().print(" ");
                System.getCout().print(doc);
                System.getCout().newline();
            }
            else {
                System.getCout().print("No documentation available ");
                System.getCout().print("(but it should be!). ");
                System.getCout().print("Please report to Gleam developers.");
                System.getCout().newline();
            }
        }
        System.getCout().newline();
        System.getCout().print("Special variable __errobj contains last offending object after an error.");
        System.getCout().newline();
        return Void.makeVoid();
    }},

    /**
     * set-verbosity!
     * Sets gleam runtime support verbosity (1..5)
     */
    new Primitive( "set-verbosity!",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Sets verbosity level: 0=off, 1=standard ... 5=pedantic",
        "E.g. (set-verbosity! 2)" /* doc strings */ ) {
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
        throws GleamException
    {
        if (!(arg1 instanceof Number)) {
            throw new GleamException(this, "invalid argument", arg1);
        }
        double v = ((Number)arg1).getDoubleValue();
        if (v < 0.0 || v > 5.0) {
            throw new GleamException(this, "invalid argument (should be between 0 and 5)", arg1);
        }
        gleam.util.Log.setVerbosity((int) v);
        return Void.makeVoid();
    }},

    /**
     * verbosity
     * Gets gleam runtime support verbosity (1..5)
     */
    new Primitive( "verbosity",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        0, 0, /* min, max no. of arguments */
        "Returns current verbosity level", null /* doc strings */ ) {
    public Entity apply0(Environment env, Continuation cont)
        throws GleamException
    {
        return new Real(gleam.util.Log.getVerbosity());
    }},

    /**
     * save-session
     * Saves the session environment.
     */
    new Primitive( "save-session",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Saves current session environment, e.g. (save-session \"file\")",
        null /* doc strings */ ) {
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
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
                throw new GleamException(this, "file not found", arg1);
            }
            catch (java.io.IOException e) {
                Log.record(e);
                throw new GleamException(this, "I/O error", arg1);
            }
        }
        else {
            throw new GleamException(this, "invalid argument", arg1);
        }
    }},

    /**
     * load-session
     * Loads the session environment.
     */
    new Primitive( "load-session",
        Primitive.INTR_ENV, Primitive.IDENTIFIER, /* environment, type */
        1, 1, /* min, max no. of arguments */
        "Loads a session environment, e.g. (load-session \"file\")",
        null /* doc strings */ ) {
    public Entity apply1(Entity arg1, Environment env, Continuation cont)
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
                Log.record(e);
                throw new GleamException(this, "file not found", arg1);
            }
            catch (java.io.IOException e) {
                Log.record(e);
                throw new GleamException(this, "I/O error", arg1);
            }
            catch (java.lang.ClassNotFoundException e) {
                Log.record(e);
                throw new GleamException(this, "class not found", arg1);
            }
            catch (java.lang.ClassCastException e) {
                Log.record(e);
                throw new GleamException(this, "invalid class", arg1);
            }
        }
        else {
            throw new GleamException(this, "invalid argument", arg1);
        }
    }},
    
    }; // primitives

}
