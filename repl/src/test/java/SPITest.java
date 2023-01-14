import gleam.lang.Real;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SPITest {

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
        assertEquals(new Real(42.0), value);
    }
}