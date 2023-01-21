import gleam.util.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.*;

public class SPITest {

    private static final Logger logger = Logger.getLogger();

    @BeforeEach
    void init() {
        logger.setLevel(Logger.Level.CONFIG);
    }

    @Test
    public void testEngineIsAutomaticallyRegistered() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("gleam");
        assertNotNull(engine);
    }

    @Test
    public void testEngineStarts() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("gleam");
        Object value = engine.eval("(+ 2 40)");
        assertEquals(42.0, ((Number)value).doubleValue());
    }

    @Test
    public void testEngineThrowsOnInvalidCode() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("gleam");
        assertThrows(ScriptException.class, () -> engine.eval(",,,"));
    }
}