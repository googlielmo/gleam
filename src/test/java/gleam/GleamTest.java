/*
 * Copyright (c) 2001-2022 Guglielmo Nigri.  All Rights Reserved.
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
import gleam.util.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class GleamTest {

    private static final Logger logger = Logger.getLogger();

    private Interpreter intp;

    @BeforeEach
    void init() throws GleamException
    {
        intp = Interpreter.newInterpreter();
        intp.traceOff();
        runTestFile("/test-utilities.scm");
    }

    @Test
    void tests_scm() throws GleamException
    {
        runTestFile("/tests.scm");
    }

    @Test
    void tests_continuations_scm() throws GleamException
    {
        runTestFile("/tests-continuations.scm");
    }

    private void runTestFile(String testFile) throws GleamException
    {
        try {
            InputStream inputStream = getClass().getResourceAsStream(testFile);
            assertNotNull(inputStream, String.format("test file %s not found", testFile));

            InputPort tests = new gleam.lang.InputPort(new java.io.InputStreamReader(inputStream));
            intp.load(tests, intp.getSessionEnv());
        } catch (GleamException e) {
            logger.warning(e);
            logger.warning("__errobj: ", e.value());
            fail(String.format("GleamException: %s", e.getMessage()));
        }
    }
}
