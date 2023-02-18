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

import java.io.PrintWriter;

/**
 * Scheme integer numerical object.
 */
public class Int extends Number implements Entity
{

    private static final long serialVersionUID = 1L;

    /** the value */
    private final int value;

    public Int(int n)
    {
        value = n;
    }

    /**
     * Writes an int
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.print(value);
        return out;
    }

    @Override
    public int intValue()
    {
        return value;
    }

    @Override
    public long longValue()
    {
        return value;
    }

    @Override
    public float floatValue()
    {
        return (float) value;
    }

    @Override
    public double doubleValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    /**
     * Returns a hashcode for this Scheme object. The implementation is the same
     * as per java.lang.Integer.
     *
     * @return int
     */
    @Override
    public int hashCode()
    {
        return value;
    }

    /**
     * Scheme int comparison.
     *
     * @param o java.lang.Object
     *
     * @return boolean
     */
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Int) {
            return value == ((Int) o).value;
        }
        else {
            return false;
        }
    }
}
