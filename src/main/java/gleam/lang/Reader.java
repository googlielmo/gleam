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

import gleam.util.Logger;

import java.io.StreamTokenizer;

import static gleam.util.Logger.Level.FINE;

/**
 * Scheme reader (lexical analyzer & parser).
 * Implemented as recursive descent.
 */
class Reader {

    protected final StreamTokenizer tkzr;

    /**
     * Creates a new reader from an input stream.
     *
     * @param r java.io.Reader
     */
    public Reader(java.io.Reader r)
    {
        tkzr = new StreamTokenizer(r);
        tkzr.resetSyntax();
        tkzr.lowerCaseMode(false);
        tkzr.slashStarComments(false);
        tkzr.commentChar(';');
        tkzr.quoteChar('\"');
        tkzr.whitespaceChars('\u0000', '\u0020');
        tkzr.eolIsSignificant(false);
        tkzr.wordChars('A','Z'); // A-Z
        tkzr.wordChars('a','z'); // a-z
        tkzr.wordChars('0','9'); // 0-9
        tkzr.wordChars('\u00A1','\u00FF'); // Unicode latin-1 supplement, symbols and letters
        tkzr.wordChars('*','.'); // '*', '+', ',', '-', '.'
        tkzr.wordChars('!','!'); // '!'
        tkzr.wordChars('#','#'); // '#' Gleam extension
        tkzr.wordChars('\\','\\'); // '\'
        tkzr.wordChars('/','/'); // '/'
        tkzr.wordChars('$','$'); // '$'
        tkzr.wordChars('_','_'); // '_'
        tkzr.wordChars('<','@'); // '<', '=', '>', '?', '@'
    }

    /**
     * Converts external into internal representation.
     */
    public Entity read()
        throws GleamException
    {
        try {
            Entity o = null;
            if (tkzr.nextToken() != StreamTokenizer.TT_EOF) {
                tkzr.pushBack();
                o = readObject();
            }
            return o;
        }
        catch (java.io.IOException e) {
            throw new GleamException("read: I/O Error "+ e.getMessage());
        }
    }

    private Entity readList(Pair l)
        throws GleamException, java.io.IOException
    {
        String t;
        boolean first = true, seendot = false;

        Pair ins = l; // which cons are we inserting stuff into?

        while (true) {
            t = readToken();
            if (t == null) {
                throw new GleamException("read: unterminated list");
            }

            if (t.equals(")")) {
                return endList(first, ins);
            }
            else if (t.equals(".")) {
                seendot = dottedPair(first, seendot, ins);
            }
            else if (!seendot) {
                tkzr.pushBack();
                if (first) {
                    first = false;
                    ins.setCar(readObject());
                }
                else {
                    ins = readCons(ins);
                }
            }
            else {
                throw new GleamException("read: missing \")\"");
            }
        }
    }

    private Entity endList(boolean first, Pair pair) {
        if (first) {
            return EmptyList.value;
        }
        else {
            return pair;
        }
    }

    private boolean dottedPair(boolean first, boolean seendot, Pair pair) throws GleamException, java.io.IOException {
        if (!seendot) {
            if (first) {
                throw new GleamException("read: at least one datum must precede \".\"");
            }
            else {
                pair.setCdr(readObject());
                seendot = true;
            }
        }
        else {
            throw new GleamException("read: more than one \".\" in a list");
        }
        return seendot;
    }

    private Pair readCons(Pair ins) throws GleamException, java.io.IOException {
        Pair nextcons = new Pair(readObject(), EmptyList.value);
        ins.setCdr(nextcons);
        ins = nextcons;
        return ins;
    }

    private Entity readObject()
        throws GleamException, java.io.IOException
    {
        String t = readToken();
        if (t == null) {
            throw new GleamException("read: unexpected end of input");
        }

        return readObject(t);
    }

    private Entity readObject(String t)
        throws GleamException, java.io.IOException
    {
        if (t.equals("")) {
            return readObject(); // special case for unquote/unquote-splicing
        }
        switch (t) {
            case "(":
                Pair l = new Pair(EmptyList.value, EmptyList.value);
                return readList(l);
            case "'": // quote
                return readQuotedObj(readObject(), Symbol.QUOTE);
            case "`": // semiquote
                return readQuotedObj(readObject(), Symbol.QUASIQUOTE);
            case ")":  // extra parens
                throw new GleamException("read: unexpected \")\"");
            default:
                return readOthers(t);
        }
    }

    private Entity readQuotedObj(Entity entity, Symbol quote) {
        return new Pair(quote, new Pair(entity, EmptyList.value));
    }

    private Entity readOthers(String t)
        throws GleamException, java.io.IOException
    {
        if (".+-0123456789".indexOf(t.charAt(0)) >= 0
        && !t.equals("+")
        && !t.equals("-")
        && !t.equals("...")) {
            try {
                logToken(t, "number");
                return new Real(Double.parseDouble(t));
            }
            catch (NumberFormatException e) {
                throw new GleamException("read: invalid number " + t);
            }
        }
        else if (t.startsWith(",@")) {
            return readQuotedObj(readObject(t.substring(2)), Symbol.UNQUOTE_SPLICING);
        }
        else if (t.startsWith(",")) {
            return readQuotedObj(readObject(t.substring(1)), Symbol.UNQUOTE);
        }
        else if (t.startsWith("\"")) {
            // it is a string
            logToken(t, "string");
            return new MutableString(t.substring(1));
        }
        else if (t.equalsIgnoreCase("#f")) {
            return Boolean.falseValue;
        }
        else if (t.equalsIgnoreCase("#t")) {
            return Boolean.trueValue;
        }
        else if (t.startsWith("#\\")) {
            return getCharacter(t);
        }
        else {
            // it is a symbol
            logToken(t, "symbol");
            return Symbol.makeSymbol(t);
        }
    }

    private void logToken(String token, String s) {
        Logger.enter(FINE, "readOthers: interpreting '" + token + "' as a " +s);
    }

    private Character getCharacter(String t) throws GleamException {
        // poor man's character parser
        String charstring = t.substring(2);
        if (charstring.equalsIgnoreCase("space")) {
            return new Character(' ');
        }
        else if (charstring.equalsIgnoreCase("newline")) {
            return new Character('\n');
        }
        else if (charstring.length() == 1) {
            return new Character(charstring.charAt(0));
        }
        else throw new GleamException("read: invalid character");
    }

    /**
     * Reads a token.
     * @return java.lang.String
     */
    private String readToken()
            throws java.io.IOException {
        String retVal;
        int c = tkzr.nextToken();

        switch(tkzr.ttype) {
            case StreamTokenizer.TT_EOF:
                retVal = null;
                break;

            case StreamTokenizer.TT_NUMBER:
                retVal = String.valueOf(tkzr.nval);
                break;

            case StreamTokenizer.TT_WORD:
                retVal = tkzr.sval;
                break;

            case '\"':
                retVal = '\"'+tkzr.sval;
                break;

            default:
                retVal = String.valueOf((char)c);
                break;
        }

        if (retVal != null)
            Logger.enter(FINE, "TOKEN="+retVal);

        return retVal;
    }
}
