/*
 * Copyright (c) 2001-2022 Guglielmo Nigri.  All Rights Reserved.
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

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class ReaderTest {

    // lists

    @Test
    public void read_empty_list() throws GleamException
    {
        Entity expected = EmptyList.value();
        String expr = "()";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_single_element_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Pair expected = new Pair(a, EmptyList.value());
        String expr = "(a)";

        Reader r = getReader(expr);
        Entity actual = r.read();
        assertThat(actual, instanceOf(Pair.class));
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    public void read_two_element_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Pair expected = new Pair(a, new Pair(b, EmptyList.value()));
        String expr = "(a b)";

        Reader r = getReader(expr);
        Entity actual = r.read();
        assertThat(actual, instanceOf(Pair.class));
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    public void read_three_element_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Pair expected = new Pair(a, new Pair(b, new Pair(c, EmptyList.value())));
        String expr = "(a b c)";

        Reader r = getReader(expr);
        Entity actual = r.read();
        assertThat(actual, instanceOf(Pair.class));
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    public void read_list_with_nested_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Pair expected = new Pair(a, new Pair(b, new Pair(c, new Pair(new Pair(Symbol.makeSymbol("d"), EmptyList.value()), EmptyList.value()))));
        String expr = "(a b c (d))";

        Reader r = getReader(expr);
        Entity actual = r.read();
        assertThat(actual, instanceOf(Pair.class));
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    public void read_two_element_dotted_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Pair expected = new Pair(a, b);
        String expr = "(a . b)";

        Reader r = getReader(expr);
        Entity actual = r.read();
        assertThat(actual, instanceOf(Pair.class));
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    @Test
    public void read_three_element_dotted_list() throws GleamException
    {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Pair expected = new Pair(a, new Pair(b, c));
        String expr = "(a b . c)";

        Reader r = getReader(expr);
        Entity actual = r.read();
        assertThat(actual, instanceOf(Pair.class));
        Pair pair = (Pair) actual;
        assertTrue(equalPairs(expected, pair));
    }

    // numbers

    @Test
    public void read_number() throws GleamException
    {
        Entity expected = new Real(1);
        String expr = "1";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    // strings

    @Test
    public void read_string() throws GleamException
    {
        Entity expected = new MutableString("hello");
        String expr = "\"hello";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_quotes() throws GleamException
    {
        Entity expected = new MutableString("\"hello");
        String expr = "\"\\\"hello";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_backslash() throws GleamException
    {
        Entity expected = new MutableString("\\");
        String expr = "\"\\\\";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_newline() throws GleamException
    {
        Entity expected = new MutableString("\n");
        String expr = "\"\\n";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_tab() throws GleamException
    {
        Entity expected = new MutableString("\t");
        String expr = "\"\\t";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_backspace() throws GleamException
    {
        Entity expected = new MutableString("\b");
        String expr = "\"\\b";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_formfeed() throws GleamException
    {
        Entity expected = new MutableString("\f");
        String expr = "\"\\f";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }

    @Test
    public void read_string_with_escaped_carriage_return() throws GleamException
    {
        Entity expected = new MutableString("\r");
        String expr = "\"\\r";

        Reader r = getReader(expr);
        assertEquals(expected, r.read());
    }




    // utils

    private Reader getReader(String expr)
    {
        return new Reader(new StringReader(expr));
    }

    private boolean equalPairs(Pair a, Pair b) {

        boolean cars;

        if (a.getCar() instanceof Pair && b.getCar() instanceof Pair) {
            cars = equalPairs((Pair) a.getCar(), (Pair) b.getCar());
        }
        else {
            cars = a.getCar().equals(b.getCar());
        }
        if (!cars) return false;

        if (b.getCdr() instanceof Pair) {
            return equalPairs((Pair) a.getCdr(), (Pair) b.getCdr());
        }
        return a.getCdr().equals(b.getCdr());
    }
}
