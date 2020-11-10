package gleam.util;

import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static gleam.util.Logger.Level.WARNING;
import static org.junit.Assert.assertNotNull;

public class GleamTest {

    @Test
    public void test()
            throws GleamException {

        runTestFile("/tests.scm");
    }

    private void runTestFile(String testFile)
            throws GleamException {
        Interpreter intp;
        try {
            intp = Interpreter.getInterpreter();
            intp.traceOff();
            InputStream inputStream = getClass().getResourceAsStream(testFile);
            assertNotNull(String.format("test file %s not found", testFile), inputStream);
            InputPort tests =
                    new gleam.lang.InputPort(
                            new java.io.InputStreamReader(inputStream));
            intp.load(tests, intp.getSessionEnv());
        } catch (GleamException e) {
            Logger.error(e);
            Logger.enter(WARNING, "__errobj:", e.value());
            Assert.fail(String.format("GleamException: %s", e.getMessage()));
        }
    }
}
