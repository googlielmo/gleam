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
 * JavaObject.java
 *
 * Created on 20 jan 2007, 14.34
 *
 */

package gleam.lang;

import gleam.util.Log;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * A Java object.
 */
public class JavaObject extends Entity {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    protected Object value;
    
    /** Creates a new instance of JavaObject */
    public JavaObject() {
        value = null;
    }

    public JavaObject(Object object) {
        value = object;
    }

    public JavaObject(Symbol s) throws GleamException {
        value = null;
        String className = s.toString();
        try {
            value = Class.forName(className).getConstructor().newInstance();
        } catch (SecurityException ex) {
            Log.record(ex);
            throw new GleamException("new: SecurityException: "+ex.getMessage(), s);
        } catch (IllegalArgumentException ex) {
            Log.record(ex);
            throw new GleamException("new: IllegalArgumentException: "+ex.getMessage(), s);
        } catch (NoSuchMethodException ex) {
            Log.record(ex);
            throw new GleamException("new: NoSuchMethodException: "+ex.getMessage(), s);
        } catch (InvocationTargetException ex) {
            Log.record(ex);
            throw new GleamException("new: InvocationTargetException: "+ex.getMessage(), s);
        } catch (InstantiationException ex) {
            Log.record(ex);
            throw new GleamException("new: InstantiationException: "+ex.getMessage(), s);
        } catch (ClassNotFoundException ex) {
            Log.record(ex);
            throw new GleamException("new: ClassNotFoundException: "+ex.getMessage(), s);
        } catch (IllegalAccessException ex) {
            Log.record(ex);
            throw new GleamException("new: IllegalAccessException: "+ex.getMessage(), s);
        }
    }

    public JavaObject(Symbol s, Class[] classes, Object[] objects) throws GleamException {
        value = null;
        String className = s.toString();
        try {
            value = Class.forName(className).getConstructor(classes).newInstance(objects);
        } catch (SecurityException ex) {
            Log.record(ex);
            throw new GleamException("new: SecurityException: "+ex.getMessage(), s);
        } catch (IllegalArgumentException ex) {
            Log.record(ex);
            throw new GleamException("new: IllegalArgumentException: "+ex.getMessage(), s);
        } catch (NoSuchMethodException ex) {
            Log.record(ex);
            throw new GleamException("new: NoSuchMethodException: "+ex.getMessage(), s);
        } catch (InvocationTargetException ex) {
            Log.record(ex);
            throw new GleamException("new: InvocationTargetException: "+ex.getMessage(), s);
        } catch (InstantiationException ex) {
            Log.record(ex);
            throw new GleamException("new: InstantiationException: "+ex.getMessage(), s);
        } catch (ClassNotFoundException ex) {
            Log.record(ex);
            throw new GleamException("new: ClassNotFoundException: "+ex.getMessage(), s);
        } catch (IllegalAccessException ex) {
            Log.record(ex);
            throw new GleamException("new: IllegalAccessException: "+ex.getMessage(), s);
        }
    }

    public void write(PrintWriter out) {
        out.print(this.toString());
    }
    
    public String toString() {
        return value == null ? "null" : value.toString();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (value == null || value instanceof Serializable) 
            out.defaultWriteObject();
        else
            out.writeObject(new JavaObject());
    }

    public Object getObjectValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof JavaObject))
            return false;
        else if (value == null) {
            return ((JavaObject) obj).value == null;
        }
        else
            return this.value.equals(((JavaObject) obj).value);
    }

    public boolean eq_p(JavaObject obj) {
        return value == obj.value;
    }
}
