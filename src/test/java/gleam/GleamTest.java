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

import gleam.lang.GleamException;
import gleam.lang.InputPort;
import gleam.lang.Interpreter;
import gleam.lang.OutputPort;
import gleam.util.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class GleamTest
{
    private static final Logger logger = Logger.getLogger();

    private static final Pattern FAILED_GLEAM_TEST = Pattern.compile(
            "^Running test:[^K]*?FAILED.*$",
            Pattern.MULTILINE);

    private Interpreter intp;

    @BeforeEach
    void init() throws GleamException
    {
        logger.setLevel(Logger.Level.CONFIG);
        intp = Interpreter.newInterpreter();
        intp.getSessionEnv().getExecutionContext().setTraceEnabled(false);
        runTestFile("/test-utilities.scm");
    }

    private void runTestFile(String testFile)
    {
        try {
            InputStream inputStream = getClass().getResourceAsStream(testFile);
            assertNotNull(inputStream,
                          String.format("test file %s not found", testFile));

            InputPort tests =
                    new gleam.lang.InputPort(
                            new java.io.InputStreamReader(inputStream));
            StringWriter stringWriter = new StringWriter();
            OutputPort outputPort = new OutputPort(stringWriter, false);
            intp.getSessionEnv().getExecutionContext().setOut(outputPort);
            intp.load(tests, intp.getSessionEnv());
            String testOutput = stringWriter.toString();
            if (checkFailures(testOutput)) {
                fail(String.format("Failures in %s", testFile));
            }
        }
        catch (GleamException e) {
            logger.warning(e);
            logger.warning("__errobj: ", e.value());
            fail(String.format("GleamException: %s", e.getMessage()));
        }
    }

    private boolean checkFailures(String testOutput)
    {
        boolean retVal = false;
        if (testOutput.contains("FAILED:")) {
            printFailures(testOutput);
            retVal = true;
        }
        if (checkUnmatchedOutput(testOutput)) {
            retVal = true;
        }
        return retVal;
    }

    private void printFailures(String testOutput)
    {
        Matcher matcher = FAILED_GLEAM_TEST.matcher(testOutput);
        while (matcher.find()) {
            System.err.printf(">>> %s%n%n", matcher.group());
        }
    }

    private boolean checkUnmatchedOutput(String testOutput)
    {
        boolean retVal = false;
        BufferedReader lineReader = new BufferedReader(new StringReader(
                testOutput));
        String prev = "";
        String line;
        try {
            while ((line = lineReader.readLine()) != null) {
                if (line.contains("Expected & actual output:")) {
                    String v1 = lineReader.readLine().trim();
                    String v2 = lineReader.readLine().trim();
                    if (!Objects.equals(v1, v2)) {
                        System.err.printf(
                                ">>> %s%n%s%n%s%n%s%n%nFAILED: expected and actual output do not match%n%n",
                                prev,
                                line,
                                v1,
                                v2);
                        retVal = true;
                    }
                }
                prev = line;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            retVal = true;
        }
        return retVal;
    }

    @Test
    void tests_scm()
    {
        runTestFile("/tests.scm");
    }

    @Test
    void tests_continuations_scm()
    {
        runTestFile("/tests-continuations.scm");
    }
}
