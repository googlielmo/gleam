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

import gleam.util.Logger;

import java.io.PrintWriter;

/**
 * Syntax rewriter. Holds a rewriting function of one argument.
 * <p>
 * Creation date: Dec. 22, 2004 15:32
 */
public final class SyntaxRewriter extends Closure implements SyntaxObject
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger();

    public SyntaxRewriter(Closure rewriter)
    {
        super(rewriter.param, rewriter.body, rewriter.definitionEnv);
    }

    /**
     * write
     *
     * @param out PrintWriter
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.write("#<syntax-rewriter");
        if (logger.getLevelValue() < Logger.Level.INFO.getValue()) {
            out.write(" ");
            new Pair(Symbol.LAMBDA, new Pair(param, body)).write(out);
        }
        out.write(">");
        return out;
    }
}
