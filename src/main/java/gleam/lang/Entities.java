/*
 * Copyright (c) 2023 Guglielmo Nigri.  All Rights Reserved.
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

import static gleam.lang.Symbol.QUOTE;

public class Entities
{
    public static EmptyList nil()
    {
        return EmptyList.VALUE;
    }

    public static Entity car(List list) throws GleamException
    {
        return list.getCar();
    }

    public static Entity cdr(List list) throws GleamException
    {
        return list.getCdr();
    }

    public static Pair cons(Entity entity1, Entity entity2)
    {
        return new Pair(entity1, entity2);
    }

    public static Symbol symbol(String string)
    {
        return Symbol.makeSymbol(string);
    }

    public static Entity quoted(Entity entity)
    {
        return cons(QUOTE, cons(entity, nil()));
    }

    public static Boolean bool(boolean bool)
    {
        return Boolean.makeBoolean(bool);
    }

    public static Pair append(Pair pair, Entity entity)
    {
        pair.setCdr(cons(entity, nil()));
        return pair;
    }

    public static List list(java.util.List<Entity> lst)
    {
        List p = EmptyList.VALUE;

        for (int i = lst.size() - 1; i >= 0; --i) {
            p = cons(lst.get(i), p);
        }
        return p;
    }

    public MutableString string(String string)
    {
        return new MutableString(string);
    }

    public static Real real(Number number)
    {
        return new Real(number.getDoubleValue());
    }

    public static Real real(double dbl)
    {
        return new Real(dbl);
    }

    public static Real real(int integer)
    {
        return new Real(integer);
    }
}
