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

package gleam.util;

import gleam.lang.Entity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import static gleam.lang.Entities.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest
{
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private static final PrintStream originalErr = System.err;
    private static Logger logger;

    @BeforeEach
    void setUp()
    {
        LogManager.getLogManager().reset();
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(java.util.logging.Level.ALL);

        errContent.reset();
        System.setErr(new PrintStream(errContent));
        java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger("gleam");
        julLogger.addHandler(getConsoleHandler());
        julLogger.setLevel(java.util.logging.Level.ALL);

        logger = Logger.getLogger();
    }

    @AfterAll
    static void restoreStreams()
    {
        System.setErr(originalErr);
        LogManager.getLogManager().reset();
    }

    @Test
    void getLogger()
    {
        assertNotNull(logger);
    }

    static ConsoleHandler getConsoleHandler()
    {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(java.util.logging.Level.ALL);
        handler.setFormatter(new TestFormatter());
        return handler;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void getLevelAsInt(int level)
    {
        logger.setLevel(level);
        assertEquals(level, logger.getLevelValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void setLevelAsIntAndLog(int level)
    {
        logger.setLevel(level);
        logger.log(0, "0");
        logger.log(1, "1");
        logger.log(2, "2");
        logger.log(3, "3");
        logger.log(4, "4");
        logger.log(5, "5");
        logger.log(6, "6");

        int numLines = errContent.toString().split(System.getProperty("line.separator")).length;
        int expectedLines = 7 - level;

        assertEquals(expectedLines, numLines);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void setLevelAsIntAndLogWithEntity(int level)
    {
        Entity entity = symbol("SomeSymbol");
        logger.setLevel(level);
        logger.log(0, "0", entity);
        logger.log(1, "1", entity);
        logger.log(2, "2", entity);
        logger.log(3, "3", entity);
        logger.log(4, "4", entity);
        logger.log(5, "5", entity);

        String content = errContent.toString();
        int numLines = content.split(System.getProperty("line.separator")).length;
        int expectedLines = 6 - level;

        assertEquals(expectedLines, numLines);
        int numSymbols = content.split("SomeSymbol").length - 1;
        assertEquals(expectedLines, numSymbols);
    }

    @ParameterizedTest
    @EnumSource(Logger.Level.class)
    void testSetAndGetLevel(Logger.Level level)
    {
        logger.setLevel(level);
        assertEquals(level, logger.getLevel());
    }

    @Test
    void logWithEntity()
    {
        logger.log(Logger.Level.INFO, "Message", symbol("SomeSymbol"));
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains("SomeSymbol"));
    }

    @Test
    void configWithEntity()
    {
        logger.config("Message", symbol("SomeSymbol"));
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains("SomeSymbol"));
    }

    @Test
    void infoWithEntity()
    {
        logger.info("Message", symbol("SomeSymbol"));
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains("SomeSymbol"));
    }

    @Test
    void severeWithEntity()
    {
        logger.severe("Message", symbol("SomeSymbol"));
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains("SomeSymbol"));
    }

    @Test
    void debug()
    {
        logger.debug("Message");
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
    }

    @Test
    void config()
    {
        logger.config("Message");
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
    }

    @Test
    void info()
    {
        logger.info("Message");
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
    }

    @Test
    void warning()
    {
        logger.warning("Message");
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
    }

    @Test
    void warningWithThrowable()
    {
        IllegalArgumentException ex = new IllegalArgumentException("SomeError");
        logger.severe(ex);
        String err = errContent.toString();
        assertTrue(err.contains(ex.getClass().getName()));
        assertTrue(err.contains("SomeError"));
    }

    @Test
    void warningWithThrowableMessage()
    {
        IllegalArgumentException ex = new IllegalArgumentException("SomeError");
        logger.warning("Message", ex);
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains(ex.getClass().getName()));
        assertTrue(err.contains("SomeError"));
    }

    @Test
    void warningWithThrowableNoMessage()
    {
        IllegalArgumentException ex = new IllegalArgumentException();
        logger.warning("Message", ex);
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains(ex.getClass().getName()));
        assertFalse(err.contains("(null)"));
    }

    @Test
    void severe()
    {
        logger.severe("Message");
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
    }

    @Test
    void severeWithThrowable()
    {
        IllegalArgumentException ex = new IllegalArgumentException("SomeError");
        logger.severe(ex);
        String err = errContent.toString();
        assertTrue(err.contains(ex.getClass().getName()));
        assertTrue(err.contains("SomeError"));
    }

    @Test
    void severeWithThrowableMessage()
    {
        IllegalArgumentException ex = new IllegalArgumentException("SomeError");
        logger.severe("Message", ex);
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains(ex.getClass().getName()));
        assertTrue(err.contains("SomeError"));
    }

    @Test
    void severeWithThrowableNoMessage()
    {
        IllegalArgumentException ex = new IllegalArgumentException();
        logger.severe("Message", ex);
        String err = errContent.toString();
        assertTrue(err.contains("Message"));
        assertTrue(err.contains(ex.getClass().getName()));
        assertFalse(err.contains("(null)"));
    }
}

class TestFormatter extends Formatter
{
    public String format(LogRecord logRecord)
    {
        StringBuilder builder =
                new StringBuilder()
                        .append(formatMessage(logRecord))
                        .append(System.lineSeparator());
        Throwable throwable = logRecord.getThrown();
        if (throwable != null) {
            builder.append(throwable).append(System.lineSeparator());
        }
        return builder.toString();
    }
}
