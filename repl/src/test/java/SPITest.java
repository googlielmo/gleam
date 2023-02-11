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

import gleam.util.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.io.StringWriter;

import static javax.script.ScriptContext.ENGINE_SCOPE;
import static javax.script.ScriptContext.GLOBAL_SCOPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SPITest
{
    private static final Logger logger = Logger.getLogger();

    ScriptEngineManager manager;

    @BeforeEach
    void init()
    {
        logger.setLevel(Logger.Level.CONFIG);
        manager = new ScriptEngineManager();
    }

    @Test
    void testEngineIsAutomaticallyRegistered()
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        assertNotNull(engine);
    }

    @Test
    void testDeleteFromGlobalScopeAndReadFromSchemeThrowsOnUnboundSymbol()
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 99.9, GLOBAL_SCOPE);
        engine.getContext().removeAttribute("attr", GLOBAL_SCOPE);
        assertThrows(ScriptException.class, () -> engine.eval("(+ 2 attr)"));
    }

    @Test
    void testEngineSimpleExpr() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        Object value = engine.eval("(+ 2 40)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testPutEngineScopeAndReadFromScheme() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 40.0, ENGINE_SCOPE);
        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testPutEngineScopeTwiceAndReadFromScheme() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 99.9, ENGINE_SCOPE);
        engine.getContext().setAttribute("attr", 40.0, ENGINE_SCOPE);
        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testEvalWithBindings() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 99.9, ENGINE_SCOPE);

        Bindings b = new SimpleBindings();
        b.put("attr", 40.0);

        Object value = engine.eval("(+ 2 attr)", b);
        assertEquals(42.0, ((Number) value).doubleValue());

        assertEquals(99.9,
                     engine.getContext().getAttribute("attr", ENGINE_SCOPE));
    }

    @Test
    void testEvalWithContext() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 99.9, ENGINE_SCOPE);

        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.getBindings(ENGINE_SCOPE).put("attr", 40.0);

        Object value = engine.eval("(+ 2 attr)", scriptContext);
        assertEquals(42.0, ((Number) value).doubleValue());

        assertEquals(99.9,
                     engine.getContext().getAttribute("attr", ENGINE_SCOPE));
    }

    @Test
    void testPutGlobalScopeAndReadFromScheme() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 40.0, GLOBAL_SCOPE);
        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testPutGlobalScopeTwiceAndReadFromScheme() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 99.9, GLOBAL_SCOPE);
        engine.getContext().setAttribute("attr", 40.0, GLOBAL_SCOPE);
        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testPutGlobalAndEngineScopeAndReadFromSchemePreservesOrder() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 11.1, GLOBAL_SCOPE);
        engine.getContext().setAttribute("attr", 40.0, ENGINE_SCOPE);
        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testPutWithDifferentMethodsAndReadFromSchemePreservesScopeOrder() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");

        // shared global scope
        engine.getContext().setAttribute("attr", 10.0, GLOBAL_SCOPE);

        // these all refer to the same scope
        engine.getContext().getBindings(ENGINE_SCOPE).put("attr", 20.0);
        engine.getBindings(ENGINE_SCOPE).put("attr", 30.0);
        engine.put("attr", 40.0);

        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());

        engine.getBindings(ENGINE_SCOPE).remove("attr");

        Object value1 = engine.eval("(+ 2 attr)");
        assertEquals(12.0, ((Number) value1).doubleValue());
    }

    @Test
    void testDeleteFromEngineScopeAndReadFromScheme() throws ScriptException
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 40.0, GLOBAL_SCOPE);
        engine.getContext().setAttribute("attr", 99.9, ENGINE_SCOPE);
        engine.getContext().removeAttribute("attr", ENGINE_SCOPE);
        Object value = engine.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value).doubleValue());
    }

    @Test
    void testDeleteFromEngineAndGlobalScopeAndReadFromSchemeThrowsOnUnboundSymbol()
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setAttribute("attr", 40.0, GLOBAL_SCOPE);
        engine.getContext().setAttribute("attr", 99.9, ENGINE_SCOPE);
        engine.getContext().removeAttribute("attr", ENGINE_SCOPE);
        engine.getContext().removeAttribute("attr", GLOBAL_SCOPE);
        assertThrows(ScriptException.class, () -> engine.eval("(+ 2 attr)"));
    }

    @Test
    void testGlobalScopeIsSharedBetweenEngineInstances() throws ScriptException
    {
        ScriptEngine engine1 = manager.getEngineByName("gleam");
        ScriptEngine engine2 = manager.getEngineByName("gleam");

        // set global in engine1
        engine1.getContext().setAttribute("attr", 40.0, GLOBAL_SCOPE);

        // use global in engine1
        Object value1 = engine1.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value1).doubleValue());

        // use global in engine2
        Object value2 = engine2.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value2).doubleValue());

        // replace global value in engine1
        engine1.eval("(set! attr 99)");

        // use global in engine2
        Object value3 = engine2.eval("(+ 2 attr)");
        assertEquals(101.0, ((Number) value3).doubleValue());
    }

    @Test
    void testEngineScopeIsNotSharedBetweenEngineInstances() throws ScriptException
    {
        ScriptEngine engine1 = manager.getEngineByName("gleam");
        ScriptEngine engine2 = manager.getEngineByName("gleam");

        engine1.put("attr", 40.0);
        engine2.getContext().setAttribute("attr", 19.0, ENGINE_SCOPE);

        Object value1 = engine1.eval("(+ 2 attr)");
        assertEquals(42.0, ((Number) value1).doubleValue());

        Object value2 = engine2.eval("(+ 2 attr)");
        assertEquals(21.0, ((Number) value2).doubleValue());
    }

    @Test
    void testUsingMultipleScopesWorksAsExpected() throws ScriptException
    {
        // See also "Java Platform, Standard Edition Java Scripting
        // Programmer's Guide", Rel. 10, March 2018, Oracle, 2-6
        StringWriter stringWriter = new StringWriter();

        ScriptEngine engine = manager.getEngineByName("gleam");
        engine.getContext().setWriter(stringWriter);

        engine.put("x", "hello,");
        engine.eval("(display x)");

        // define a different script context
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setWriter(stringWriter);
        newContext.setBindings(engine.createBindings(),
                               ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("x", " world");
        engine.eval("(display x)", newContext);

        assertEquals("hello, world", stringWriter.toString());
    }

    @Test
    void testEngineThrowsOnInvalidCode()
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        assertThrows(ScriptException.class, () -> engine.eval(",,,"));
    }
}