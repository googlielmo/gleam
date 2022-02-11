/*
 * Copyright (c) 2001-2020 Guglielmo Nigri.  All Rights Reserved.
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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

public class ClosureTest {

    @Test
    public void getMaxArity_EmptyList() throws GleamException, IOException {
        Closure c1 = getClosureWithParams(EmptyList.VALUE);
        Assert.assertEquals(0, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_OneParam() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("a");
        EmptyList nil = EmptyList.VALUE;
        Closure c1 = getClosureWithParams(new Pair(a, nil));
        Assert.assertEquals(1, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_TwoParams() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        EmptyList nil = EmptyList.VALUE;
        Closure c1 = getClosureWithParams(new Pair(a, new Pair(b, nil)));
        Assert.assertEquals(2, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_ThreeParams() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        EmptyList nil = EmptyList.VALUE;
        Closure c1 = getClosureWithParams(new Pair(a, new Pair(b, new Pair(c, nil))));
        Assert.assertEquals(3, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_FourParams() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Symbol d = Symbol.makeSymbol("d");
        EmptyList nil = EmptyList.VALUE;
        Closure c1 = getClosureWithParams(new Pair(a, new Pair(b, new Pair(c, new Pair(d, nil)))));
        Assert.assertEquals(4, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_FiveParams() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        Symbol d = Symbol.makeSymbol("d");
        Symbol e = Symbol.makeSymbol("e");
        EmptyList nil = EmptyList.VALUE;
        Closure c1 = getClosureWithParams(new Pair(a, new Pair(b, new Pair(c, new Pair(d, new Pair(e, nil))))));
        Assert.assertEquals(5, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_TwoParamsAndRest() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("rest");
        Closure c1 = getClosureWithParams(new Pair(a, new Pair(b, c)));
        Assert.assertEquals(-1, c1.getMaxArity());
    }

    @Test
    public void getMaxArity_RestOnly() throws GleamException, IOException {
        Symbol a = Symbol.makeSymbol("rest");
        Closure c1 = getClosureWithParams(a);
        Assert.assertEquals(-1, c1.getMaxArity());
    }

    private Closure getClosureWithParams(Entity value) throws IOException {
        return new Closure(value,
                           EmptyList.VALUE,
                           Environment.newEnvironment(
                                   new InputPort(new InputStreamReader(java.lang.System.in)),
                                   new OutputPort(java.lang.System.out, false)));
    }
}
