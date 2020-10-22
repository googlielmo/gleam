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
 * Scheme input port.
 */
public class InputPort extends Port
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    java.io.Reader value;
    private final transient Reader gleamReader;

    public InputPort(String name)
        throws java.io.FileNotFoundException
    {
        this( new java.io.BufferedReader(
                new java.io.InputStreamReader(
                    new java.io.FileInputStream(name) )));
    }

    public InputPort(java.io.Reader reader) {
        this.value = reader;
        this.gleamReader = new Reader(value);
    }

    @Override
    public void close()
        throws java.io.IOException
    {
        if (value != null) {
            value.close();
        }
        value = null;
    }

    @Override
    public boolean isOpen() {
        return null != value;
    }

    /**
     * reads (parses) an object
    */
    public Entity read()
        throws GleamException
    {
        Entity retVal = gleamReader.read();
        if (retVal == null)
            return Eof.value;
        else
            return retVal;
    }

    /**
     * Writes a port
     */
    @Override
    public void write(java.io.PrintWriter out)
    {
        out.print("#<input-port>");
    }

}

