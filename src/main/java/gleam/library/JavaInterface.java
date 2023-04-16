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
 * JavaInterface.java
 *
 * Created on January 20, 2007, 14.51
 */

package gleam.library;

import gleam.lang.Continuation;
import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.GleamException;
import gleam.lang.Int;
import gleam.lang.JavaObject;
import gleam.lang.ListIterator;
import gleam.lang.MutableString;
import gleam.lang.Real;
import gleam.lang.Symbol;
import gleam.lang.Void;
import gleam.util.EntityObjectConverter;
import gleam.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gleam.lang.Environment.Kind.INTERACTION_ENV;
import static gleam.lang.JavaObject.makeJavaObject;
import static gleam.lang.JavaObject.makeJavaObjectInstance;

/**
 * JAVA INTERFACE
 * <p>
 * Primitive operator and procedure implementation library.
 */
public class JavaInterface
{
    private static final Logger logger = Logger.getLogger();
    private static final EntityObjectConverter entityObjectConverter = new EntityObjectConverter();

    /**
     * This array contains definitions of primitives. It is used by static initializers in
     * {@code gleam.lang.System} to populate the initial environments.
     */
    public static final Primitive[] primitives = {

            /*
             * new
             */
            new Primitive("new",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          Primitive.VAR_ARGS, /* min, max no. of arguments */
                          "Makes a new Java object, e.g. (new 'java.util.Date)",
                          null /* doc strings */)
            {
                @Override
                public Entity apply(gleam.lang.List args,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    ListIterator it = new ListIterator(args);
                    Entity e = it.next();
                    if (!(e instanceof Symbol)) {
                        throw new GleamException(this,
                                                 "wrong argument type, should be a symbol",
                                                 e);
                    }
                    Symbol className = (Symbol) e;
                    if (!it.hasNext()) {
                        return makeJavaObjectInstance(className);
                    }
                    List<Class<?>> argClasses = new ArrayList<>();
                    Collection<Object> argObjects = new ArrayList<>();
                    iterateArguments(it, argClasses, argObjects);
                    return makeJavaObjectInstance(className,
                                                  argClasses.toArray(new Class<?>[0]),
                                                  argObjects.toArray());
                }
            },

            /*
             * call
             */
            new Primitive("call",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          2,
                          Primitive.VAR_ARGS, /* min, max no. of arguments */
                          "Calls a method on a Java object",
                          "E.g. (call 'length (new 'java.lang.String \"test\")) => 4" /* doc strings */)
            {
                @Override
                public Entity apply(gleam.lang.List args,
                                    Environment env,
                                    Continuation cont) throws GleamException
                {
                    ListIterator it = new ListIterator(args);
                    Entity methodName = it.next();
                    if (!(methodName instanceof Symbol)) {
                        throw new GleamException(this,
                                                 "wrong argument type, should be a symbol",
                                                 methodName);
                    }
                    Entity object = it.next();
                    if (!(object instanceof JavaObject)) {
                        throw new GleamException(this,
                                                 "wrong argument type, should be a Java object",
                                                 object);
                    }
                    List<Class<?>> argClasses = new ArrayList<>();
                    Collection<Object> argObjects = new ArrayList<>();
                    iterateArguments(it, argClasses, argObjects);
                    return call((JavaObject) object,
                                (Symbol) methodName,
                                argClasses.toArray(new Class<?>[0]),
                                argObjects.toArray());
                }
            },

            /*
             * class-of
             */
            new Primitive("class-of",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns the class of its argument",
                          "E.g. (class-of (new 'java.lang.String \"test\")) => class java.lang.String" /* doc strings */)
            {
                @Override
                public Entity apply(Entity arg1, Environment env, Continuation cont)
                {
                    if (arg1 instanceof JavaObject) {
                        JavaObject javaObject = (JavaObject) arg1;
                        if (javaObject.getObjectValue() == null) {
                            return javaObject;
                        }
                        else {
                            return makeJavaObject(javaObject.getObjectValue().getClass());
                        }
                    }
                    return makeJavaObject(arg1.getClass());
                }
            },

            /*
             * object->entity (value-of)
             */
            new Primitive("object->entity",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns a Gleam Entity for a (Java) Object",
                          "E.g. (object->entity (new 'java.lang.String \"test\")) => \"test" /* doc strings */)
            {
                // TODO
            },

            /*
             * object->entity (value-of)
             */
            new Primitive("entity->object",
                          INTERACTION_ENV,
                          Primitive.IDENTIFIER, /* environment, type */
                          1,
                          1, /* min, max no. of arguments */
                          "Returns a Gleam Entity for a (Java) Object",
                          "E.g. (object->entity (new 'java.lang.String \"test\")) => \"test" /* doc strings */)
            {
                // TODO
            }
    }; // primitives

    /** Can't instantiate this class. */
    private JavaInterface() {}

    private static void iterateArguments(ListIterator it,
                                         List<Class<?>> argClasses,
                                         Collection<Object> argObjects) throws GleamException
    {
        while (it.hasNext()) {
            Entity arg = it.next();
            argClasses.add(getJavaClass(arg));
            argObjects.add(getObjectFromEntity(arg));
        }
    }

    private static Class<?> getJavaClass(Entity arg) throws GleamException
    {
        if (arg instanceof JavaObject) {
            Object o = ((JavaObject) arg).getObjectValue();
            return o == null ? null : o.getClass();
        }
        else if (arg instanceof MutableString) {
            return String.class;
        }
        else if (arg instanceof Symbol) {
            return String.class;
        }
        else if (arg instanceof gleam.lang.Boolean) {
            return boolean.class;
        }
        else if (arg instanceof Real) {
            return double.class;
        }
        else if (arg instanceof Int) {
            return int.class;
        }
        else {
            throw new GleamException("cannot obtain the Java Class for a Gleam entity", arg);
        }
    }

    private static Object getObjectFromEntity(Entity arg) throws GleamException
    {
        Object ret = entityObjectConverter.convert(arg);
        if (ret == arg) {
            throw new GleamException("cannot obtain the Java Object for a Gleam entity", arg);
        }
        return ret;
    }

    private static Entity getEntityFromObject(Object object)
    {
        return entityObjectConverter.invert(object);
    }

    private static Entity call(JavaObject object,
                               Symbol methodName,
                               Class<?>[] parameterTypes,
                               Object[] arguments) throws GleamException
    {
        if (object.getObjectValue() == null) {
            throw new GleamException("call: null pointer", methodName);
        }
        Class<?> clazz = object.getObjectValue().getClass();
        try {
            Method method = clazz.getMethod(methodName.toString(), parameterTypes);
            Object retVal = method.invoke(object.getObjectValue(), arguments);
            if (method.getReturnType().isInstance(java.lang.Void.class)) {
                return Void.VALUE;
            }
            return getEntityFromObject(retVal);
        }
        catch (SecurityException |
               IllegalArgumentException |
               NoSuchMethodException |
               IllegalAccessException |
               InvocationTargetException ex) {
            logger.warning(ex);
            String msg = "call: " + ex.getClass().getSimpleName() + ex.getMessage();
            throw new GleamException(msg, methodName, ex);
        }
    }
}
