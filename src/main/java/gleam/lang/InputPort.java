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

    private java.io.BufferedReader reader;

    private transient Parser gleamParser;

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
        reader = new BufferedReader(
                new java.io.InputStreamReader(
                        Files.newInputStream(Paths.get(name))));
        gleamParser = new Parser(reader);
    }

    public InputPort(java.io.Reader reader)
    {
        this.fileName = null;
        this.reader = reader instanceof BufferedReader
                      ? ((BufferedReader) reader)
                      : new BufferedReader(reader);
        this.gleamParser = new Parser(this.reader);
    }

    /**
     * Close this InputPort.
     */
    @Override
    public void close()
    {
        if (isOpen()) {
            gleamParser = null;
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
     * <code>false</code> otherwise.
     */
    @Override
    public boolean isOpen()
    {
        return null != gleamParser;
    }

    @Override
    public Kind getKind()
    {
        return Kind.TEXTUAL;
    }

    /**
     * Writes a port.
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.print("#<input-port>");
        return out;
    }

    /**
     * reads (parses) an object.
     */
    public Entity read() throws GleamException
    {
        checkOpen();
        Entity retVal = gleamParser.read();
        if (retVal == null) {
            return Eof.VALUE;
        }
        else {
            return retVal;
        }
    }

    public Entity readChar() throws GleamException
    {
        checkOpen();
        try {
            int c = reader.read();
            return getCharOrEof(c);
        }
        catch (IOException e) {
            throw new GleamException("read-char: I/O Error " + e.getMessage());
        }
    }

    public Entity peekChar() throws GleamException
    {
        checkOpen();
        try {
            gleamParser.read();
            reader.mark(1024);
            int c = reader.read();
            reader.reset();
            return getCharOrEof(c);
        }
        catch (IOException e) {
            throw new GleamException("peek-char: I/O Error " + e.getMessage());
        }
    }

    private static Entity getCharOrEof(int c)
    {
        if (c == -1) {
            return Eof.VALUE;
        }
        return new Character((char) c);
    }

    private void checkOpen() throws GleamException
    {
        if (!isOpen()) {
            throw new GleamException("closed input port");
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
            gleamParser = null;
        }
    }
}
