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
import gleam.lang.GleamException;
import gleam.lang.Interpreter;
import gleam.lang.JavaObject;
import gleam.lang.Real;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.Reader;

public class GleamScriptEngine extends AbstractScriptEngine
{

    private final Interpreter interpreter;

    public GleamScriptEngine()
    {
        try {
            this.interpreter = Interpreter.newInterpreter();
        } catch (GleamException e) {
            throw new RuntimeException(e);
        }
        setContext(new GleamScriptContext(this.interpreter));
    }

    static Entity wrap(Object value)
    {
        if (value instanceof Entity) {
            return (Entity) value;
        } else if (value instanceof Number) {
            return new Real(((Number) value).doubleValue());
        }
        return new JavaObject(value);
    }

    static Object unwrap(Object value)
    {
        if (value instanceof JavaObject) {
            return ((JavaObject) value).getObjectValue();
        } else if (value instanceof gleam.lang.Number) {
            return ((gleam.lang.Number) value).getDoubleValue();
        }
        return value;
    }

    public Interpreter getInterpreter()
    {
        return interpreter;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException
    {
        try {
            Object value = interpreter.eval(script);
            return unwrap(value);
        } catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException
    {
        try {
            Object value = interpreter.eval(reader);
            return unwrap(value);
        } catch (GleamException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Bindings createBindings()
    {
        return new GleamBindings(Interpreter.getInteractionEnv());
    }

    @Override
    public ScriptEngineFactory getFactory()
    {
        return new GleamScriptEngineFactory();
    }
}

