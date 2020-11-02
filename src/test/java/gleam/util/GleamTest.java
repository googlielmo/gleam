package gleam.util;

import gleam.lang.Entity;
import gleam.lang.Environment;
import gleam.lang.Eof;
import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import gleam.lang.OutputPort;
import gleam.lang.System;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static gleam.util.Log.Level.*;
import static org.junit.Assert.assertNotNull;

public class GleamTest {

    @Test
    public void test()
            throws GleamException {
        String testFile = "/tests.scm";
        Interpreter intp = Interpreter.getInterpreter();

        runFile(testFile, intp);
    }

    private void runFile(String testFile, Interpreter intp)
            throws GleamException {
        InputStream inputStream = getClass().getResourceAsStream(testFile);
        assertNotNull(String.format("test file %s not found", testFile), inputStream);
        InputPort tests =
                new gleam.lang.InputPort(
                        new java.io.InputStreamReader(inputStream));
        try {
            tests.load(intp.getSessionEnv());
        } catch (GleamException e) {
            Log.error(e);
            Log.enter(WARNING, "__errobj:", e.value());
            Assert.fail(String.format("GleamException: %s", e.getMessage()));
        }
    }
}
