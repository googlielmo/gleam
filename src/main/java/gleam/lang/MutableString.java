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

import java.io.PrintWriter;
import java.util.Objects;

/**
 * Scheme mutable string.
 * <p>
 * Creation date: (28/10/2001 12.22.48)
 */
public class MutableString extends AbstractEntity
{

    private static final long serialVersionUID = 1L;

    /** The current string value. */
    final StringBuilder value;

    /**
     * Creates a new Scheme string. Creation date: (28/10/01 12.24.51)
     *
     * @param s java.lang.String
     */
    public MutableString(String s)
    {
        Objects.requireNonNull(s);
        value = new StringBuilder(s);
    }

    /**
     * Writes a Scheme string.
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.print("\"");
        for (int i = 0; i < value.length(); ++i) {
            switch (value.charAt(i)) {
                case '\t':
                    out.print("\\t");
                    break;
                case '\r':
                    out.print("\\r");
                    break;
                case '\n':
                    out.print("\\n");
                    break;
                case '\\':
                    out.print("\\\\");
                    break;
                case '"':
                    out.print("\\\"");
                    break;
                default:
                    out.print(value.charAt(i));
            }
        }
        out.print("\"");
        return out;
    }

    /**
     * Displays a Scheme string.
     */
    @Override
    public PrintWriter display(PrintWriter out)
    {
        out.print(value);
        return out;
    }

    /**
     * Obtains the current string value as a java.lang.String.
     */
    @Override
    public String toString()
    {
        return value.toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutableString that = (MutableString) o;
        return value.toString().equals(that.toString());
    }
}
