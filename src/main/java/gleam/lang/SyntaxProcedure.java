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

/*
 * SyntaxProcedure.java
 *
 * Created on January, 16 2002, 19.42
 */

package gleam.lang;

import java.io.PrintWriter;

/**
 * A special form, or a specialized version of primitive procedure, with
 * different rules for parameter evaluation.
 * It is assumed that procedures of this class may take
 * their arguments without following the standard
 * evaluation rules.
 */
public class SyntaxProcedure extends PrimitiveProcedure implements SyntaxObject {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * SyntaxProcedure
     */
    public SyntaxProcedure(gleam.library.Primitive p) {
        super(p);
    }

    @Override
    public void write(PrintWriter out)
    {
        out.write("#<syntax-procedure "+ value.getName() + ">");
    }

}
