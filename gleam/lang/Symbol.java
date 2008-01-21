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

package gleam.lang;

import java.util.Map;
import java.util.Collections;

/**
 * Scheme symbol factory.
 */
public final class Symbol extends Entity
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique symbol table
	 */
	static Map symtable =
		Collections.synchronizedMap(new java.util.HashMap(512));

	/**
	 * common symbols (some are keywords, some are not)
	 * defined here as constants for convenience
	 */
	public final static Symbol QUOTE = makeSymbol("quote");
	public final static Symbol LAMBDA = makeSymbol("lambda");
	public final static Symbol SET = makeSymbol("set!");
	public final static Symbol BEGIN = makeSymbol("begin");
	public final static Symbol LET = makeSymbol("let");
	public final static Symbol LETSTAR = makeSymbol("let*");
	public final static Symbol LETREC = makeSymbol("letrec");
	public final static Symbol DO = makeSymbol("do");
	public final static Symbol DELAY = makeSymbol("delay");
	public final static Symbol QUASIQUOTE = makeSymbol("quasiquote");
	public final static Symbol UNQUOTE = makeSymbol("unquote");
	public final static Symbol UNQUOTE_SPLICING = makeSymbol("unquote-splicing");
	public final static Symbol DEFINE = makeSymbol("define");
	public final static Symbol IF = makeSymbol("if");
	public final static Symbol COND = makeSymbol("cond");
	public final static Symbol CASE = makeSymbol("case");
	public final static Symbol ELSE = makeSymbol("else");
	public final static Symbol ARROW = makeSymbol("=>");
	public final static Symbol AND = makeSymbol("and");
	public final static Symbol OR = makeSymbol("or");
	public final static Symbol CONS = makeSymbol("cons");
	public final static Symbol LIST = makeSymbol("list");
	public final static Symbol APPEND = makeSymbol("append");
	public final static Symbol ERROBJ = makeSymbol("__errobj");
	public final static Symbol CALL_WITH_CURRENT_CONTINUATION = makeSymbol("call-with-current-continuation");
	public final static Symbol CALL_CC = makeSymbol("call/cc");

	/**
	 * String representation
	 */
	String value;

	/**
	 * Interned?
	 */
	boolean interned;

	/**
	 * Can't instantiate directly.
	 */
	private Symbol(String value)
	{
		this.value = value;
		this.interned = true;
	}

	private Symbol(String value, boolean interned)
	{
		this.value = value;
		this.interned = interned;
	}

	/**
	 * Obtains the string representation of this symbol
	 */
	public String toString() {
		return value;
	}

	/**
	 * Evaluates this symbol in the given environment.
	 */
	public Entity eval(Environment env, Continuation cont)
		throws GleamException
	{
		return env.lookup(this);
	}

	/**
	 * Factory method to create and intern a symbol.
	 */
	public static Symbol makeSymbol(String s)
	{
		java.lang.Object o = symtable.get(s);
		if (o == null) {
			o = new Symbol(s);
			symtable.put(s, o);
		}
		return (Symbol) o;
	}

	/**
	 * Factory method to create an uninterned symbol.
	 */
	public static Symbol makeUninternedSymbol(String s)
	{
		return new Symbol(s, false);
	}


	/**
	 * Prevents the release of multiple instances upon deserialization.
	 */
	protected java.lang.Object readResolve()
		throws java.io.ObjectStreamException
	{
//		java.lang.System.out.println("readResolve() called! (Symbol)"); //DEBUG
		if (interned)
			return makeSymbol(value);
		else
			return this;
	}

	/**
	 * Performs environment optimization on this symbol.
	 */
	public Entity optimize(Environment env)
		throws GleamException
	{
		try {
			Location loc = env.getLocation(this);
			if (loc.get() == Undefined.value) {
				/* this symbol is a function parameter, so let
				 * name resolution take place at run time
				 */
				return this;
			}
			else {
				return loc;
			}
		}
		catch (GleamException ex) {
			// if unbound, return just the symbol (for syntax rewriters)
			return this;
		}
	}

	/** Writes this symbol */
	public void write(java.io.PrintWriter out)
	{
		out.write(value);
	}
}
