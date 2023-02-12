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

package gleam;

import gleam.lang.Entity;
import gleam.lang.ExecutionContext;
import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import gleam.lang.JavaObject;
import gleam.lang.OutputPort;
import gleam.lang.Pair;
import gleam.lang.Symbol;
import gleam.util.Converter;
import gleam.util.Logger;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.ArrayList;

import static gleam.lang.Entities.bool;
import static gleam.lang.Entities.car;
import static gleam.lang.Entities.cdr;
import static gleam.lang.Entities.cons;
import static gleam.lang.Entities.list;
import static gleam.lang.Entities.quoted;
import static gleam.lang.Entities.real;
import static gleam.lang.Entities.symbol;
import static javax.script.ScriptContext.ENGINE_SCOPE;

public class GleamScriptEngine implements ScriptEngine, Invocable
{
    private static final Logger logger = Logger.getLogger();

    private static final Converter<Entity, Object>
            entityObjectConverter = new Converter<Entity, Object>()
    {
        @Override
        public Object convert(Entity entity)
        {
            if (entity instanceof JavaObject) {
                return ((JavaObject) entity).getObjectValue();
            }
            else if (entity instanceof gleam.lang.Boolean) {
                return ((gleam.lang.Boolean) entity).getBooleanValue();
            }
            else if (entity instanceof gleam.lang.Number) {
                return ((gleam.lang.Number) entity).getDoubleValue();
            }
            return entity;
        }

        @Override
        public Entity invert(Object value)
        {
            if (value instanceof Entity) {
                return (Entity) value;
            }
            else if (value instanceof Boolean) {
                return bool((Boolean) value);
            }
            else if (value instanceof Number) {
                return real(((Number) value).doubleValue());
            }
            return new JavaObject(value);
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
    };
    private static final Symbol CALL = Symbol.makeSymbol("call");

    private final Interpreter interpreter;

    private GleamScriptContext context;

    public GleamScriptEngine()
    {
        try {
            this.interpreter = Interpreter.newInterpreter();
            this.setContext(new GleamScriptContext(this.interpreter));
        }
        catch (GleamException e) {
            logger.warning(e.getMessage());
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            logger.severe(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    static Entity entityOf(Object value)
    {
        return entityObjectConverter.invert(value);
    }

    static Object objectOf(Entity value)
    {
        return entityObjectConverter.convert(value);
    }

    public Interpreter getInterpreter()
    {
        return interpreter;
    }

    @Override
    public Object eval(String script,
                       ScriptContext context) throws ScriptException
    {
        ScriptContext prevContext = this.getContext();
        this.setContext(toGleamContext(context));
        Object ret = this.eval(script);
        this.setContext(prevContext);
        return ret;
    }

    private GleamScriptContext toGleamContext(ScriptContext context)
    {
        ExecutionContext executionContext =
                new ExecutionContext(this.interpreter,
                                     new InputPort(context.getReader()),
                                     new OutputPort(context.getWriter(),
                                                    false),
                                     new OutputPort(context.getErrorWriter(),
                                                    false));
        return new GleamScriptContext(executionContext,
                                      context.getBindings(ENGINE_SCOPE));
    }

    @Override
    public Object eval(Reader reader,
                       ScriptContext context) throws ScriptException
    {
        ScriptContext prevContext = this.getContext();
        this.setContext(context);
        Object ret = this.eval(reader);
        this.setContext(prevContext);
        return ret;
    }

    @Override
    public Object eval(String script) throws ScriptException
    {
        try {
            return entityObjectConverter.convert(interpreter.eval(script));
        }
        catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(Reader reader) throws ScriptException
    {
        try {
            return entityObjectConverter.convert(interpreter.eval(reader));
        }
        catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(String script, Bindings n) throws ScriptException
    {
        Bindings prevBindings = this.getContext().getBindings(ENGINE_SCOPE);
        this.getContext().setBindings(n, ENGINE_SCOPE);
        Object ret = this.eval(script);
        this.getContext().setBindings(prevBindings, ENGINE_SCOPE);
        return ret;
    }

    @Override
    public Object eval(Reader reader, Bindings n) throws ScriptException
    {
        Bindings prevBindings = this.getContext().getBindings(ENGINE_SCOPE);
        this.getContext().setBindings(n, ENGINE_SCOPE);
        Object ret = this.eval(reader);
        this.getContext().setBindings(prevBindings, ENGINE_SCOPE);
        return ret;
    }

    @Override
    public void put(String key, Object value)
    {
        getBindings(ENGINE_SCOPE).put(key, value);
    }

    @Override
    public Object get(String key)
    {
        return getBindings(ENGINE_SCOPE).get(key);
    }

    @Override
    public Bindings getBindings(int scope)
    {
        return context.getBindings(scope);
    }

    @Override
    public void setBindings(Bindings bindings, int scope)
    {
        switch (scope) {
            case ScriptContext.GLOBAL_SCOPE:
                context.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
                break;
            case ENGINE_SCOPE:
                context.setBindings(bindings, ENGINE_SCOPE);
                break;
            default:
                throw new IllegalArgumentException("Invalid scope value.");
        }
    }

    @Override
    public Bindings createBindings()
    {
        return new GleamBindings(Interpreter.getInteractionEnv());
    }

    @Override
    public ScriptContext getContext()
    {
        return context;
    }

    @Override
    public void setContext(ScriptContext context)
    {
        if (context instanceof GleamScriptContext) {
            GleamScriptContext gleamScriptContext = (GleamScriptContext) context;
            this.context = gleamScriptContext;
            ExecutionContext executionContext = gleamScriptContext.getExecutionContext();
            this.interpreter.getSessionEnv()
                            .setExecutionContext(executionContext);
        }
        else {
            throw new IllegalArgumentException(
                    "ScriptContext type not supported.");
        }
    }

    @Override
    public ScriptEngineFactory getFactory()
    {
        return new GleamScriptEngineFactory();
    }

    @Override
    public Object invokeMethod(Object thiz,
                               String name,
                               Object... args)
            throws ScriptException, NoSuchMethodException
    {
        try {
            Pair call;
            Entity self = entityObjectConverter.invert(thiz);
            call = toGleamList(name, args);
            if (self instanceof JavaObject) {
                call = cons(CALL,
                            cons(quoted(car(call)),
                                 cons(self, cdr(call))));
            }
            return evalPair(call);
        }
        catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object invokeFunction(String name,
                                 Object... args)
            throws ScriptException, NoSuchMethodException
    {
        return evalPair(toGleamList(name, args));
    }

    @Override
    public <T> T getInterface(Class<T> clasz)
    {
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz)
    {
        return null;
    }

    private Pair toGleamList(String name, Object[] args)
    {
        java.util.List<Entity> entityList = new ArrayList<>();
        for (Object arg : args) {
            entityList.add(entityObjectConverter.invert(arg));
        }
        return cons(symbol(name), list(entityList));
    }

    private Object evalPair(Pair call)
            throws ScriptException, NoSuchMethodException
    {
        try {
            return entityObjectConverter.convert(interpreter.eval(call));
        }
        catch (GleamException e) {
            if (e.getCause() instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) e.getCause();
            }
            throw new ScriptException(e);
        }
    }
}
