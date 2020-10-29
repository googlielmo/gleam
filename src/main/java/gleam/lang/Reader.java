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

import java.io.StreamTokenizer;

import static gleam.util.Log.Level.FINE;

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
                if (first) {
                    return EmptyList.value;
                }
                else {
                    return l;
                }
            }
            else if (t.equals(".")) {
                if (!seendot) {
                    if (first) {
                        throw new GleamException("read: at least one datum must precede \".\"");
                    }
                    else {
                        ins.setCdr(readObject());
                        seendot = true;
                    }
                }
                else {
                    throw new GleamException("read: more than one \".\" in a list");
                }
            }
            else if (!seendot) {
                tkzr.pushBack();
                if (first) {
                    first = false;
                    ins.setCar(readObject());
                }
                else {
                    Pair nextcons = new Pair(readObject(), EmptyList.value);
                    ins.setCdr(nextcons);
                    ins = nextcons;
                }
            }
            else {
                throw new GleamException("read: missing \")\"");
            }
        }
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
            case "'": { // quote
                Entity quotedobj = readObject();
                return new Pair(Symbol.QUOTE, new Pair(quotedobj, EmptyList.value));
            }
            case "`": { // semiquote
                Entity quotedobj = readObject();
                return new Pair(Symbol.QUASIQUOTE, new Pair(quotedobj, EmptyList.value));
            }
            case ")":  // extra parens
                throw new GleamException("read: unexpected \")\"");
            default:
                return readOthers(t);
        }
    }

    private Entity readOthers(String t)
        throws GleamException, java.io.IOException
    {
        if (".+-0123456789".indexOf(t.charAt(0)) >= 0
        && !t.equals("+")
        && !t.equals("-")
        && !t.equals("...")) {
            try {
                gleam.util.Log.enter(FINE, "readOthers: interpreting '" + t + "' as a number");
                return new Real(Double.parseDouble(t));
            }
            catch (NumberFormatException e) {
                throw new GleamException("read: invalid number " + t);
            }
        }
        else if (t.startsWith(",@")) {
            Entity unquotedobj = readObject(t.substring(2));
            return new Pair(Symbol.UNQUOTE_SPLICING, new Pair(unquotedobj, EmptyList.value));
        }
        else if (t.startsWith(",")) {
            Entity unquotedobj = readObject(t.substring(1));
            return new Pair(Symbol.UNQUOTE, new Pair(unquotedobj, EmptyList.value));
        }
        else if (t.startsWith("\"")) {
            // it is a string
            gleam.util.Log.enter(FINE, "readOthers: interpreting '" + t + "' as a string");
            return new MutableString(t.substring(1));
        }
        else if (t.equalsIgnoreCase("#f")) {
            return Boolean.falseValue;
        }
        else if (t.equalsIgnoreCase("#t")) {
            return Boolean.trueValue;
        }
        else if (t.startsWith("#\\")) {
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
        else {
            // it is a symbol
            gleam.util.Log.enter(FINE, "readOthers: interpreting '" + t + "' as a symbol");
            return Symbol.makeSymbol(t);
        }
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
            gleam.util.Log.enter(FINE, "TOKEN="+retVal);

        return retVal;
    }
}
