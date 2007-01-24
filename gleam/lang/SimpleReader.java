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

import gleam.util.Report;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Scheme reader (lexical analyzer & parser).
 * Implemented as recursive descent.
 */
final class SimpleReader
{
	public final static String white_space = " \t\n\r\f";
	public final static String std_delim = " \t\n\r\f\";,()'`#{}[]|";

	protected StringTokenizer tkzr;
	protected boolean moreTokens;
	protected String ungetc = null;
	
	/**
	 * Converts external into internal representation.
	 */
	public Vector read(String s)
		throws GleamException
	{
		Vector v = new Vector();
		Entity o;

		if (s == null)
			throw new GleamException("read: null input", null);
		
		if (s.length() > 0) {
			moreTokens = true;
			tkzr = new StringTokenizer(s, std_delim, true);
			for(;;) {
				String t = readToken();
				if (t == null) {
					Report.println(5, "read: no more objects => ending");
					break;
				}
				o = readObject(t);
				v.add(o);
			}
		}
		return v;
	}

	protected void readComment()
	{
		String t;
		while (tkzr.hasMoreTokens()) {
			t = tkzr.nextToken("\n");
			if (t.equals("\n"))
				return;
		}
	}

	protected Entity readList(Pair l) 
		throws GleamException
	{
		String t;
		boolean first = true, seendot = false;

		Pair ins = l; // which cons are we inserting stuff into?
		
		for(;;) {
			t = readToken();
			
			if (t == null) {
				throw new GleamException("read: unterminated list", null);
			}
			else if (t.equals(")")) {
				if (first && !seendot) {
					return EmptyList.value;
				}
				else {
					return l;
				}
			}
			else if (t.equals(".")) {
				if (!seendot) {
					if (first) {
						throw new GleamException("read: at least one datum must precede \".\"", null);
					}
					else {
						ins.cdr = readObject();
						seendot = true;
					}
				}
				else {
					throw new GleamException("read: more than one \".\" in a list", null);
				}
			}
			else if (!seendot) {
				if (first) {
					first = false;
					ins.car = readObject(t);
				}
				else {
					Pair nextcons = new Pair(readObject(t), EmptyList.value);
					ins.cdr = nextcons;
					ins = nextcons;
				}
			}
			else {
				throw new GleamException("read: missing \")\"", null);
			}
		}
	}

	protected Entity readObject()
		throws GleamException
	{
		return readObject(readToken());
	}

	protected Entity readObject(String t) 
		throws GleamException
	{ 
		if (t == null) {
			throw new GleamException("read: unexpected end of input", null);		
		}
		else if (t.equals("(")) {
			Pair l = new Pair(EmptyList.value, EmptyList.value);
			return readList(l);
		}
		else if (t.equals("'")) { // quote
			Entity quotedobj = readObject();
			Pair l = new Pair(Symbol.QUOTE,
				new Pair(quotedobj, EmptyList.value));
			return l;
		}
		else if (t.equals("`")) { // semiquote
			Entity quotedobj = readObject();
			Pair l = new Pair(Symbol.QUASIQUOTE,
				new Pair(quotedobj, EmptyList.value));
			return l;
		}
		else if (t.equals(")")) { // extra parens
			throw new GleamException("read: unexpected \")\"", null);
		}
		else {
			return readOthers(t);
		}
	}

	protected Entity readOthers(String t)
		throws GleamException
	{
		if (".+-0123456789".indexOf(t.charAt(0)) >= 0
				&& !t.equals("+")
				&& !t.equals("-")
				&& !t.equals("...")) {
			try {
				Report.println(5, "readOthers: interpreting '" + t + "' as a number");
				return new Real(Double.parseDouble(t));
			}
			catch (java.lang.NumberFormatException e) {
				throw new GleamException("read: invalid number " + t, null);
			}
		}
		else if (t.startsWith("\"")) {
			// it is a string
			Report.println(5, "readOthers: interpreting '" + t + "' as a string");
			return new MutableString(t.substring(1));
		}
		else {
			// it is a symbol
			Report.println(5, "readOthers: interpreting '" + t + "' as a symbol");
			return Symbol.makeSymbol(t);
		}
	}

	protected String readString()
		throws GleamException
	{
		// we mark the token with a starting double-quote
		String t = "\"";
		String t1;
		boolean seenslash = false;
		while (tkzr.hasMoreTokens()) {
			t1 = tkzr.nextToken("\\\"");
			if (t1.equals("\\") && !seenslash) {
				seenslash = true;
				continue;
			}
			if (t1.equals("\"") && !seenslash) {
				return t;
			}
			t = t + t1;
			seenslash = false;
		}
		throw new GleamException("read: unterminated string", null);
	}

	/**
	 * Reads a single token
	 */
	protected String readToken()
		throws GleamException
	{
		String t, t1;
		while (tkzr.hasMoreTokens() || ungetc != null) {
			if (ungetc == null) {
				t = tkzr.nextToken(std_delim);
			}
			else {
				t = ungetc;
				ungetc = null;
			}
			
			if (white_space.indexOf(t) >= 0) {
				// whitespace, skip
			}
			else if (t.equals(";")) {
				// comment, skip
				readComment();
			}
/*			else if (t.equals(".")) { // TODO: startsWith
				// see next char
				if (tkzr.hasMoreTokens()) {
					t1 = tkzr.nextToken(std_delim);
					if (Character.isDigit(t1.charAt(0))) {
						t = t + t1;
					}
					else {
						ungetc = t1;
					}
				}
				
				Report.println(5, "readToken: [" + t + "]");
				return t;
			}
*/			else if (t.equals(",")) {
				// see next char
				if (tkzr.hasMoreTokens()) {
					t1 = tkzr.nextToken(std_delim);
					if (t1.charAt(0) == '@') {
						t = t + "@";
						ungetc = t1.substring(1);
					}
					else {
						ungetc = t1;
					}
				}

				Report.println(5, "readToken: [" + t + "]");
				return t;
			}
			else if (t.equals("\"")) {
				t = readString();

				Report.println(5, "readToken: [" + t + "]");
				return t;
			}
			else {
				Report.println(5, "readToken: [" + t + "]");
				return t;
			}
		}
		moreTokens = false;
		Report.println(5, "readToken: null");
		return null;
	}
}

