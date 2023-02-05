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

import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Specialization of Environment with special serialization rules.
 */
public final class SystemEnvironment extends Environment
{

    private static final long serialVersionUID = 3L;

    private final Kind kind;

    /**
     * Constructor for the null environment.
     * <p>
     * It creates the default {@link ExecutionContext} binding global I/O ports
     * to system standard I/O
     */
    SystemEnvironment()
    {
        this(null, Kind.NULL_ENV);
        boolean isConsole = java.lang.System.console() != null;
        setExecutionContext(
                new ExecutionContext(
                        null,
                        new InputPort(new InputStreamReader(java.lang.System.in)),
                        new OutputPort(java.lang.System.out, isConsole),
                        new OutputPort(java.lang.System.err, isConsole)));
    }

    SystemEnvironment(Environment parent, Kind kind)
    {
        super(parent);
        this.kind = kind;
    }

    /**
     * Writes this environment
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.write(String.format("#<system-environment: %s>", kind));
        return out;
    }

    /** avoids to serialize data of system environments */
    private Object writeReplace()
    {
        return new SystemEnvironment(null, kind);
    }
}
