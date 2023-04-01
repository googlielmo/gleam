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
 * Scheme character.
 */
public final class Character extends AbstractEntity
{

    private static final long serialVersionUID = 1L;

    final char value;

    /**
     * Constructor.
     */
    public Character(char v)
    {
        value = v;
    }

    /**
     * Writes this character.
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        if (value == '\n') {
            out.print("#\\newline");
        }
        else if (value == ' ') {
            out.print("#\\space");
        }
        else {
            out.print("#\\" + value);
        }
        return out;
    }

    /**
     * Displays this character.
     */
    @Override
    public PrintWriter display(PrintWriter out)
    {
        out.print(value);
        return out;
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
        Character character = (Character) o;
        return value == character.value;
    }

    @Override
    public int hashCode()
    {
        return java.lang.Character.hashCode(value);
    }
}
