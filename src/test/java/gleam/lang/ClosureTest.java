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

import junit.framework.TestCase;
import org.junit.Assert;

public class ClosureTest extends TestCase {

    public void testGetMaxArity_EmptyList() throws GleamException {
        Closure c1 = getClosureWithParams(EmptyList.value());
        Assert.assertEquals(0, c1.getMaxArity());
    }

    public void testGetMaxArity_OneParam() throws GleamException {
        Symbol a = Symbol.makeSymbol("a");
        EmptyList nil = EmptyList.value();
        Closure c1 = getClosureWithParams(new Pair(a, nil));
        Assert.assertEquals(1, c1.getMaxArity());
    }

    public void testGetMaxArity_ManyParams() throws GleamException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        EmptyList nil = EmptyList.value();
        Closure c1 = getClosureWithParams(
                new Pair(a,
                        new Pair(b,
                                new Pair(c, nil))));
        Assert.assertEquals(3, c1.getMaxArity());
    }

    public void testGetMaxArity_ManyParamsAndRest() throws GleamException {
        Symbol a = Symbol.makeSymbol("a");
        Symbol b = Symbol.makeSymbol("b");
        Symbol c = Symbol.makeSymbol("c");
        EmptyList nil = EmptyList.value();
        Closure c1 = getClosureWithParams(
                new Pair(a,
                        new Pair(b, c)));
        Assert.assertEquals(-1, c1.getMaxArity());
    }

    public void testGetMaxArity_RestOnly() throws GleamException {
        Symbol a = Symbol.makeSymbol("a");
        EmptyList nil = EmptyList.value();
        Closure c1 = getClosureWithParams(a);
        Assert.assertEquals(-1, c1.getMaxArity());
    }

    private Closure getClosureWithParams(Entity value) {
        return new Closure(value,
                EmptyList.value(),
                Environment.newEnvironment(
                        Interpreter.getInteractionEnv().getIn(),
                        Interpreter.getInteractionEnv().getOut()));
    }
}
