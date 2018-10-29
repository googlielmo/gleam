/*
 * Copyright (c) 2001-2008 Guglielmo Nigri.  All Rights Reserved.
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

import java.util.logging.*;

/*
 * Log.java
 *
 * Based on the older Report.java, created on October 18, 2001, 1:02 AM
 */

/**
 * Logging utility class for Gleam.
 */
public class Log
{
    /* Current level of verbosity */
    private final static Logger logger;
    static {
        logger = Logger.getLogger("gleam");
        logger.setLevel(Level.INFO);
    }

    /** Can't instantiate this class */
    private Log() {}

    /**
     * Sets current verbosity level.
     * 1 = SEVERE, internal debugging information
     * 2 = WARNING, information of interest to implementers
     * 3 = INFO, unusual or remarkable activities
     * 4 = CONFIG, configuration activities
     * 5 = FINE, detailed output
     * @param verbosity
     */
    public static void setVerbosity(int verbosity) {
        logger.setLevel(getLevel(verbosity));
    }
    
    public static int getVerbosity() {
        return getVerbosity(logger.getLevel());
    }

    /**
     * Obtains a Level from an integer level value
     * @param verbosity integer level value
     * @return the corresponding Level
     */
    private static Level getLevel(int verbosity) {
        Level n;
        switch (verbosity) {
            case 0:
                n = Level.OFF;
                break;
            case 1:
                n = Level.SEVERE;
                break;
            case 2:
                n = Level.WARNING;
                break;
            case 3:
                n = Level.INFO;
                break;
            case 4:
                n = Level.CONFIG;
                break;
            default:
                n = Level.FINE;
        }
        return n;
    }

    /**
     * Obtains a Level from an integer level value
     * @param verbosity integer level value
     * @return the corresponding Level
     */
    private static int getVerbosity(Level level) {
        int n;
        switch (level.intValue()) {
            case Integer.MAX_VALUE:
                n = 0;
                break;
            case 1000: // Level.SEVERE
                n = 1;
                break;
            case 900: // Level.WARNING
                n = 2;
                break;
            case 800: // Level.INFO
                n = 3;
                break;
            case 700: // Level.CONFIG
                n = 4;
                break;
            default: // Level.FINE
                n = 5;
        }
        return n;
    }

    /**
     * Logs a message, respecting current verbosity.
     */
    public static void record(int severity, String message)
    {
        logger.log(getLevel(severity), message);
    }

    /**
     * Logs a message and an Entity, respecting current verbosity.
     */
    public static void record(int severity, String message, gleam.lang.Entity obj)
    {
        logger.log(getLevel(severity), message + " " + obj.toString());
    }

    /**
     * Logs a Throwable at verbosity 1 (SEVERE)
     * @param ex Throwable
     */
    public static void record(Throwable ex) {
        logger.log(Level.SEVERE, "Throwable", ex);
    }
}
