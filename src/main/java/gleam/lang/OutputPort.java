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

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Scheme output port object.
 */
public class OutputPort extends Port implements Closeable
{
    private static final long serialVersionUID = 3L;

    private final String fileName;

    private final boolean isConsole;

    private transient java.io.PrintWriter out;

    /**
     * creates an output port from a java.io.PrintStream object.
     *
     * @param out       a PrintStream
     * @param isConsole true if this is the system Console
     */
    public OutputPort(java.io.PrintStream out, boolean isConsole)
    {
        this(new PrintWriter(out, true), isConsole);
    }

    /**
     * creates an output port from a java.io.PrintWriter object.
     */
    public OutputPort(Writer writer, boolean isConsole)
    {
        this.fileName = null;
        this.out = writer instanceof PrintWriter
                   ? (PrintWriter) writer
                   : new PrintWriter(writer, true);
        this.isConsole = isConsole;
    }

    /**
     * creates an output port to a file
     */
    public OutputPort(String fileName) throws java.io.IOException
    {
        this.fileName = fileName;
        this.isConsole = false;
        openFile(fileName);
    }

    public PrintWriter getPrintWriter()

    {
        return out;
    }

    private void openFile(String name) throws IOException
    {
        FileOutputStream stream = new FileOutputStream(name);
        this.out = new java.io.PrintWriter(stream, true);
    }

    /**
     * closes this port
     */
    @Override
    public void close()
    {
        if (out != null) {
            out.close();
        }
        out = null;
    }

    /**
     * tests whether port is open
     */
    @Override
    public boolean isOpen()
    {
        return null != out;
    }

    /**
     * which kind of port
     */
    @Override
    public Kind getKind()
    {
        return Kind.TEXTUAL;
    }

    /**
     * @return whether this is the system Console
     */
    public boolean isConsole()
    {
        return isConsole;
    }

    /**
     * Prints an object in machine-readable form.
     *
     * @return this {@link OutputPort}
     */
    public OutputPort write(Entity obj) throws GleamException
    {
        if (isOpen()) {
            obj.write(out);
        }
        else {
            throw new GleamException("OutputPort not open");
        }
        return this;
    }

    /**
     * Prints an object in human-readable form.
     *
     * @return this {@link OutputPort}
     */
    public OutputPort display(Entity obj)
    {
        if (obj == EmptyList.VALUE) {
            out.print("()");
        }
        else {
            obj.display(out);
        }
        return this;
    }

    /**
     * Prints a newline and flushes buffers.
     *
     * @return this {@link OutputPort}
     */
    public OutputPort newline()
    {
        out.println();
        flush();
        return this;
    }

    /**
     * Flushes buffers.
     */
    public void flush()
    {
        out.flush();
        if (isConsole) {
            java.lang.System.console().flush();
        }
    }

    /**
     * String printing method, useful for primitives.
     *
     * @return this {@link OutputPort}
     */
    public OutputPort print(String s)
    {
        out.print(s);
        return this;
    }

    /**
     * Writes this port.
     *
     * @return this {@link OutputPort}
     */
    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.print("#<output-port>");
        return out;
    }

    /**
     * Outputs a printf-style formatted string.
     *
     * @param format the format String
     * @param args   argument list
     *
     * @return this {@link OutputPort}
     *
     * @see java.util.Formatter printf-style format strings
     */
    public OutputPort printf(String format, Object... args)
    {
        out.printf(format, args);
        return this;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    // serialization
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        if (this.fileName != null) {
            openFile(this.fileName);
        }
        else {
            out = null;
        }
    }
}
