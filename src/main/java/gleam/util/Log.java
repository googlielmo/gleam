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
    /**
     * Severity
     * OFF, no output
     * SEVERE, internal debugging information
     * WARNING, information of interest to implementers
     * INFO, unusual or remarkable activities
     * CONFIG, configuration activities
     * FINE, detailed output
     */
    public enum Level {
        OFF(6),
        SEVERE(5),
        WARNING(4),
        INFO(3),
        CONFIG(2),
        FINE(1),
        ALL(0);

        private final int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /* Using java.util.logging */
    final static Logger logger;

    static {
        logger = Logger.getLogger("gleam");
        if (logger.getHandlers().length == 0) {
            logger.setUseParentHandlers(false);
            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(java.util.logging.Level.ALL);
            logger.addHandler(consoleHandler);
        }
        logger.setLevel(java.util.logging.Level.INFO);
    }

    /** Can't instantiate this class */
    private Log() {}

    /**
     * Sets current level.
     * Message with level lower than this level will be discarded.
     * 6 = OFF
     * 5 = SEVERE, internal debugging information
     * 4 = WARNING, information of interest to implementers
     * 3 = INFO, unusual or remarkable activities
     * 2 = CONFIG, configuration activities
     * 1 = FINE, detailed output
     * 0 = ALL
     * @param level
     */
    public static void setLevel(int level) {
        logger.setLevel(getLoggingLevel(level));
    }
    
    public static int getLevel() {
        return getLevel(logger.getLevel());
    }

    /**
     * Obtains a java.util.logging Level from an integer level value
     * @param severity integer level value
     * @return the corresponding Level
     */
    private static java.util.logging.Level getLoggingLevel(int severity) {
        java.util.logging.Level n;
        if (severity < 0)
            severity = 0;
        switch (severity) {
            case 0:
                n = java.util.logging.Level.ALL;
                break;
            case 1:
                n = java.util.logging.Level.FINE;
                break;
            case 2:
                n = java.util.logging.Level.CONFIG;
                break;
            case 3:
                n = java.util.logging.Level.INFO;
                break;
            case 4:
                n = java.util.logging.Level.WARNING;
                break;
            case 5:
                n = java.util.logging.Level.SEVERE;
                break;
            default:
                n = java.util.logging.Level.OFF;
        }
        return n;
    }

    /**
     * Obtains a Level from an integer level value
     * @param level
     * @return the corresponding integer level value
     */
    private static int getLevel(java.util.logging.Level level) {
        final int n;

        if (level.intValue() == Integer.MAX_VALUE) {
            n = 6;
        } else if (level.intValue() >= 1000) {
            n = 5;
        } else if (level.intValue() >= 900) {
            n = 4;
        } else if (level.intValue() >= 800) {
            n = 3;
        } else if (level.intValue() >= 700) {
            n = 2;
        } else if (level.intValue() >= 500) {
            n = 1;
        } else {
            n = 0;
        }
        return n;
    }

    /**
     * Logs a message, respecting current level.
     */
    public static void record(int level, String message)
    {
        logger.log(getLoggingLevel(level), message);
    }

    /**
     * Logs a message, respecting current level.
     */
    public static void record(Level level, String message) {
        record(level.getValue(), message);
    }

    /**
     * Logs a message and an Entity, respecting current level.
     */
    public static void record(int severity, String message, gleam.lang.Entity obj)
    {
        logger.log(getLoggingLevel(severity), message + " " + obj.toString());
    }

    /**
     * Logs a message and an Entity, respecting current level.
     */
    public static void record(Level level, String message, gleam.lang.Entity obj)
    {
        record(level.getValue(), message, obj);
    }

    /**
     * Logs a Throwable at level SEVERE
     * @param ex Throwable
     */
    public static void record(Throwable ex) {
        logger.log(java.util.logging.Level.SEVERE, "Throwable", ex);
    }
}
