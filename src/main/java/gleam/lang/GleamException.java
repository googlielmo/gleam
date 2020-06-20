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

import gleam.library.Primitive;
import gleam.util.Log;

import static gleam.util.Log.Level.FINE;

/**
 * The base class for Gleam error objects.
 */
public class GleamException extends java.lang.Exception
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    protected final Entity errobj;

    private GleamException()
    {
        super();
        errobj = null;
    }

    public GleamException(Primitive primitive, String message, Entity obj)
    {
        this(primitive.toString() + ": " + message, obj);
    }
    
    public GleamException(String message, Entity obj)
    {
        super(message);
        if (obj != null)
            errobj = obj;
        else
            errobj = Void.value;

        if (System.getInteractionEnv() != null)
            System.getInteractionEnv().define(Symbol.ERROBJ, errobj);

        Log.record(FINE, "Generated GleamException: " + message);
    }

    /**
     * Gets the entity associated with this error.
     * 
     * @return gleam.lang.Entity
     */
    public Entity getObject() {
        return errobj;
    }
}
