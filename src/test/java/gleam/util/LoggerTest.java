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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggerTest
{

    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalErr = System.err;
    private static Logger logger;

    @BeforeAll
    static void setUpJul()
    {
        LogManager.getLogManager().reset();
        LogManager.getLogManager()
                  .getLogger("")
                  .setLevel(java.util.logging.Level.ALL);
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

    static ConsoleHandler getConsoleHandler()
    {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(java.util.logging.Level.ALL);
        handler.setFormatter(new TestFormatter());
        return handler;
    }

    @BeforeEach
    void setUp()
    {
        errContent.reset();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void getLevel(int level)
    {
        logger.setLevel(level);
        assertEquals(level, logger.getLevelValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void setLevel(int level)
    {
        logger.setLevel(level);
        logger.log(0, "0");
        logger.log(1, "1");
        logger.log(2, "2");
        logger.log(3, "3");
        logger.log(4, "4");
        logger.log(5, "5");
        logger.log(6, "6");


        int numLines = errContent.toString()
                                 .split(System.getProperty("line.separator")).length;
        int expectedLines = (6 - level) + 1;

        assertEquals(expectedLines, numLines);
    }

    private static class TestFormatter extends Formatter
    {

        public String format(LogRecord logRecord)
        {
            return formatMessage(logRecord) + System.lineSeparator();
        }
    }
}
