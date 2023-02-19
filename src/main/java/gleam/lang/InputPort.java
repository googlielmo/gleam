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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Scheme input port.
 */
public class InputPort extends Port implements Closeable
{
    private static final long serialVersionUID = 1L;

    private final String fileName;

    private java.io.Reader reader;

    private transient Reader gleamReader;

    public InputPort(String name) throws java.io.IOException
    {
        this.fileName = name;
        openFile(name);
    }

    public java.io.Reader getReader()
    {
        return reader;
    }

    private void openFile(String name) throws IOException
    {
        reader =
                new BufferedReader(new java.io.InputStreamReader(
                        Files.newInputStream(Paths.get(name))));
        gleamReader = new Reader(reader);
    }

    public InputPort(java.io.Reader reader)
    {
        this.fileName = null;
        this.reader = reader;
        this.gleamReader = new Reader(reader);
    }

    /**
     * Close this InputPort
     */
    @Override
    public void close()
    {
        if (isOpen()) {
            gleamReader = null;
        }
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException ignored) {
                //
            }
            reader = null;
        }
    }

    /**
     * @return <code>true</code> if this InputPort is open,
     * <code>false</code> otherwise
     */
    @Override
    public boolean isOpen()
    {
        return null != gleamReader;
    }

    @Override
    public Kind getKind()
    {
        return Kind.TEXTUAL;
    }

    /**
     * Writes a port
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.print("#<input-port>");
        return out;
    }

    /**
     * reads (parses) an object
     */
    public Entity read() throws GleamException
    {
        if (isOpen()) {
            Entity retVal = gleamReader.read();
            if (retVal == null) {
                return Eof.VALUE;
            }
            else {
                return retVal;
            }
        }
        else {
            throw new GleamException("InputPort not open");
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        if (fileName != null) {
            openFile(fileName);
        }
        else {
            gleamReader = null;
        }
    }
}
