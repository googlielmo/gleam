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

/**
 * The Scheme void value (a singleton).
 * Creation date: (31/10/01 23.06.59)
 */
public final class Void extends AbstractEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** the Void singleton */
    public static final Void VALUE = new Void();

    /** Can't instantiate this */
    private Void() {
    }

    /**
     * Prevents the release of multiple instances upon deserialization.
     */
    private Object readResolve()
    {
        return VALUE;
    }

    /**
     * Writes the void value.
     */
    @Override
    public void write(PrintWriter out)
    {
        out.write("#<void>");
    }
}
