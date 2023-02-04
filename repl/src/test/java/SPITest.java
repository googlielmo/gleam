import gleam.util.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
    void testEngineThrowsOnInvalidCode()
    {
        ScriptEngine engine = manager.getEngineByName("gleam");
        assertThrows(ScriptException.class, () -> engine.eval(",,,"));
    }
}