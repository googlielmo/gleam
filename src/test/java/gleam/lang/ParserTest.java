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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest
{

    private static final Logger logger = Logger.getLogger();

    @BeforeEach
    void init()
    {
        logger.setLevel(Logger.Level.CONFIG);
    }

    // lists

    @Test
    void read_empty_list() throws GleamException
    {
        Entity expected = EmptyList.VALUE;
        String expr = "()";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    private Parser getParser(String expr)
    {
        return new Parser(new StringReader(expr));
    }

    @Test
    void read_single_element_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Pair expected = new Pair(a, EmptyList.VALUE);
        String expr = "(a)";

        Parser r = getParser(expr);
        Entity actual = r.read();
        assertTrue(actual instanceof Pair);
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    void read_two_element_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Pair expected = new Pair(a, new Pair(b, EmptyList.VALUE));
        String expr = "(a b)";

        Parser r = getParser(expr);
        Entity actual = r.read();
        assertTrue(actual instanceof Pair);
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    void read_three_element_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Pair expected = new Pair(a, new Pair(b, new Pair(c, EmptyList.VALUE)));
        String expr = "(a b c)";

        Parser r = getParser(expr);
        Entity actual = r.read();
        assertTrue(actual instanceof Pair);
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    void read_list_with_nested_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Pair expected = new Pair(a,
                                 new Pair(b,
                                          new Pair(c,
                                                   new Pair(new Pair(Symbol.makeSymbol(
                                                           "d"),
                                                                     EmptyList.VALUE),
                                                            EmptyList.VALUE))));
        String expr = "(a b c (d))";

        Parser r = getParser(expr);
        Entity actual = r.read();
        assertTrue(actual instanceof Pair);
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    void read_two_element_dotted_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Pair expected = new Pair(a, b);
        String expr = "(a . b)";

        Parser r = getParser(expr);
        Entity actual = r.read();
        assertTrue(actual instanceof Pair);
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    void read_three_element_dotted_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Pair expected = new Pair(a, new Pair(b, c));
        String expr = "(a b . c)";

        Parser r = getParser(expr);
        Entity actual = r.read();
        assertTrue(actual instanceof Pair);
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    // numbers

    @Test
    void read_real_number() throws GleamException
    {
        Entity expected = new Real(Math.PI);
        String expr = String.valueOf(Math.PI);

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_int_number() throws GleamException
    {
        Entity expected = new Int(Integer.MAX_VALUE);
        String expr = String.valueOf(Integer.MAX_VALUE);

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    // strings

    @Test
    void read_string() throws GleamException
    {
        Entity expected = new MutableString("hello");
        String expr = "\"hello";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_quotes() throws GleamException
    {
        Entity expected = new MutableString("\"hello");
        String expr = "\"\\\"hello";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_backslash() throws GleamException
    {
        Entity expected = new MutableString("\\");
        String expr = "\"\\\\";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_newline() throws GleamException
    {
        Entity expected = new MutableString("\n");
        String expr = "\"\\n";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_tab() throws GleamException
    {
        Entity expected = new MutableString("\t");
        String expr = "\"\\t";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_backspace() throws GleamException
    {
        Entity expected = new MutableString("\b");
        String expr = "\"\\b";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_formfeed() throws GleamException
    {
        Entity expected = new MutableString("\f");
        String expr = "\"\\f";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    @Test
    void read_string_with_escaped_carriage_return() throws GleamException
    {
        Entity expected = new MutableString("\r");
        String expr = "\"\\r";

        Parser r = getParser(expr);
        assertEquals(expected, r.read());
    }

    // utils

    private boolean equalPairs(Pair a, Pair b)
    {

        boolean cars;

        if (a.getCar() instanceof Pair && b.getCar() instanceof Pair) {
            cars = equalPairs((Pair) a.getCar(), (Pair) b.getCar());
        }
        else {
            cars = a.getCar().equals(b.getCar());
        }
        if (!cars) {
            return false;
        }

        if (b.getCdr() instanceof Pair) {
            return equalPairs((Pair) a.getCdr(), (Pair) b.getCdr());
        }
        return a.getCdr().equals(b.getCdr());
    }
}
