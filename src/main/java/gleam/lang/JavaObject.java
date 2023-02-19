/*
 * Copyright (c) 2007-2023 Guglielmo Nigri.  All Rights Reserved.
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
 * JavaObject.java
 *
 * Created on January 20, 2007, 14.34
 */

package gleam.lang;

import gleam.util.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Java object.
 */
public class JavaObject extends AbstractEntity
{

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger();

    private static final JavaObject NULL = new JavaObject();

    private final Object value;

    private JavaObject()
    {
        value = null;
    }

    private JavaObject(Object object)
    {
        value = object;
    }

    public static JavaObject makeJavaObject(Object object)
    {
        if (object == null) {
            return NULL;
        }
        if (object instanceof JavaObject) {
            return ((JavaObject) object);
        }
        return new JavaObject(object);
    }

    public static JavaObject makeJavaObjectInstance(Symbol className) throws GleamException
    {
        String name = className.toString();
        Object object;
        try {
            object = Class.forName(name).getConstructor().newInstance();
        }
        catch (Exception ex) {
            logger.warning(ex);
            throw new GleamException("new: " + ex.getMessage(), className);
        }
        return makeJavaObject(object);
    }

    public static JavaObject makeJavaObjectInstance(Symbol className,
                                                    Class<?>[] classes,
                                                    Object[] objects) throws GleamException
    {
        String name = className.toString();
        Object object;
        try {
            object = Class.forName(name)
                          .getConstructor(classes)
                          .newInstance(objects);
        }
        catch (Exception ex) {
            logger.warning(ex);
            throw new GleamException("new: " + ex.getMessage(), className);
        }
        return makeJavaObject(object);
    }

    @Override
    public PrintWriter write(PrintWriter out)
    {
        out.print(this);
        return out;
    }

    @Override
    public String toString()
    {
        return value == null ? "null" : value.toString();
    }

    public Object getObjectValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JavaObject)) {
            return false;
        }
        JavaObject javaObject = (JavaObject) obj;
        return Objects.equals(value, javaObject.value);
    }

    public boolean eq(JavaObject obj)
    {
        if (obj == null) {
            return false;
        }
        return value == obj.value;
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        if (value == null || value instanceof Serializable) {
            out.defaultWriteObject();
        }
        else {
            out.writeObject(NULL);
        }
    }
}
