/*
 * Copyright (c) 2023 Guglielmo Nigri.  All Rights Reserved.
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

package gleam.util;

import gleam.lang.Entity;
import gleam.lang.JavaObject;
import gleam.lang.List;
import gleam.lang.ListIterator;
import gleam.lang.MutableString;
import gleam.lang.Symbol;
import gleam.lang.Undefined;
import gleam.lang.Void;

import java.util.ArrayList;

import static gleam.lang.Entities.bool;
import static gleam.lang.Entities.integer;
import static gleam.lang.Entities.real;
import static gleam.lang.Entities.string;
import static gleam.lang.JavaObject.makeJavaObject;

public class EntityObjectConverter implements Converter<Entity, Object>
{
    @Override
    public Object convert(Entity entity)
    {
        if (entity instanceof JavaObject) {
            return ((JavaObject) entity).getObjectValue();
        }
        else if (entity instanceof MutableString) {
            return entity.toString();
        }
        else if (entity instanceof Symbol) {
            return entity.toString();
        }
        else if (entity instanceof gleam.lang.Boolean) {
            return ((gleam.lang.Boolean) entity).getBooleanValue();
        }
        else if (entity instanceof gleam.lang.Real) {
            return ((gleam.lang.Real) entity).doubleValue();
        }
        else if (entity instanceof gleam.lang.Int) {
            return ((gleam.lang.Int) entity).intValue();
        }
        else if (entity instanceof List) {
            return javaList((List) entity);
        }
        else if (entity instanceof Void
                 || entity instanceof Undefined) {
            return null;
        }
        return entity;
    }

    private Object javaList(List list)
    {
        java.util.List<Object> javaList = new ArrayList<>();
        new ListIterator(list, true)
                .forEachRemaining(e -> javaList.add(convert(e)));
        return javaList;
    }

    @Override
    public Entity invert(Object value)
    {
        if (value instanceof Entity) {
            return (Entity) value;
        }
        else if (value instanceof String) {
            // TODO decide if and when to return a Symbol
            return string((String) value);
        }
        else if (value instanceof Boolean) {
            return bool((Boolean) value);
        }
        else if (value instanceof Number) {
            Number number = (Number) value;
            if (number.doubleValue() == number.intValue()) {
                return integer(number.intValue());
            }
            return real(number.doubleValue());
        }
        return makeJavaObject(value);
    }

    @Override
    public Object convertAny(Object value)
    {
        return convert((Entity) value);
    }

    @Override
    public Entity invertAny(Object value)
    {
        return invert(value);
    }
}
