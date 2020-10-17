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

/**
 * Scheme double precision numerical object.
 */
public class Real extends Number
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** the value */
    private double value;

    public Real(double n)
    {
        value = n;
    }

    public Real(int n)
    {
        value = n;
    }

    /**
     * Scheme double comparison.
     * Creation date: (28/10/01 12.45.00)
     * @return boolean
     * @param o java.lang.Object
     */
    @Override
    public boolean equals(java.lang.Object o) {
        if (o instanceof Real) {
            return value == ((Real)o).value;
        }
        else {
            return false;
        }
    }

    /**
     * Returns a hashcode for this Scheme object.
     * The implementation is the same as per java.lang.Double.
     * @return int
     */
    @Override
    public int hashCode() {
        long v = Double.doubleToLongBits(value);
        return (int)(v^(v>>>32));
    }

    /**
     * Writes a real
     */
    @Override
    public void write(java.io.PrintWriter out)
    {
        out.print(value);
    }

    /** Takes value of number as a double.  */
    @Override
    public double getDoubleValue() {
        return value;
    }
}

