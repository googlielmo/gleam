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

package gleam.lang;

import gleam.util.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A Scheme symbol. A symbol is <i>interned</i> by default, i.e. all occurrences of the same symbol
 * in the program text is guaranteed to refer to the same object in memory.
 */
@SuppressWarnings("unused")
public final class Symbol extends AbstractEntity
{

    /**
     * The unique symbol table
     */
    private static final Map<String, Symbol> symtable = new HashMap<>(512);

    /*
     * common symbols (some are keywords, some are not)
     * defined here as constants for convenience
     */
    public static final Symbol AND = makeSymbol("and");
    public static final Symbol BEGIN = makeSymbol("begin");
    public static final Symbol CALL_CC = makeSymbol("call/cc");
    public static final Symbol CALL_WITH_CURRENT_CONTINUATION =
            makeSymbol("call-with-current-continuation");
    public static final Symbol CASE = makeSymbol("case");
    public static final Symbol COND = makeSymbol("cond");
    public static final Symbol DEFINE = makeSymbol("define");
    public static final Symbol ERROBJ = makeSymbol("__errobj");
    public static final Symbol HELP = makeSymbol("help");
    public static final Symbol IF = makeSymbol("if");
    public static final Symbol LAMBDA = makeSymbol("lambda");
    public static final Symbol LET = makeSymbol("let");
    public static final Symbol LETREC = makeSymbol("letrec");
    public static final Symbol LETSTAR = makeSymbol("let*");
    public static final Symbol OR = makeSymbol("or");
    public static final Symbol QUASIQUOTE = makeSymbol("quasiquote");
    public static final Symbol QUOTE = makeSymbol("quote");
    public static final Symbol SET = makeSymbol("set!");
    public static final Symbol UNQUOTE = makeSymbol("unquote");
    public static final Symbol UNQUOTE_SPLICING = makeSymbol("unquote-splicing");

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger();

    /**
     * String representation
     */
    final String value;

    /**
     * Interned?
     */
    final boolean interned;

    private Symbol(String value)
    {
        this(value, true);
    }

    private Symbol(String value, boolean interned)
    {
        this.value = value;
        this.interned = interned;
    }

    /**
     * Factory method to create and intern a symbol.
     */
    public static synchronized Symbol makeSymbol(String s)
    {
        return symtable.computeIfAbsent(s, Symbol::new);
    }

    /**
     * Factory method to create an uninterned symbol.
     */
    public static Symbol makeUninternedSymbol(String s)
    {
        return new Symbol(s, false);
    }

    /**
     * Evaluates this symbol in the given environment.
     */
    @Override
    public Entity eval(Environment env, Continuation cont) throws GleamException
    {
        return env.lookup(this);
    }

    /**
     * Performs environment optimization on this symbol.
     */
    @Override
    public Entity optimize(Environment env)
    {
        Location loc = env.getLocationOrNull(this);
        if (loc == null) {
            // if unbound, return just the symbol (for syntax rewriters)
            return this;
        }
        if (loc.get() == Undefined.VALUE) {
            /* this symbol is a function parameter, so let
             * name resolution take place at run time
             */
            return this;
        }

        return loc;
    }

    /**
     * Writes this symbol
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.write(value);
        return out;
    }

    /**
     * Obtains the string representation of this symbol.
     */
    @Override
    public String toString()
    {
        return value;
    }

    /**
     * Prevents the release of multiple instances upon deserialization.
     */
    private Object readResolve()
    {
        logger.log(Logger.Level.DEBUG,
                   "readResolve() called! (Symbol)"); //DEBUG
        if (interned) {
            return makeSymbol(value);
        }
        else {
            return this;
        }
    }
}
