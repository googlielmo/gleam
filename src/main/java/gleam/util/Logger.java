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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/*
 * Gleam logger.
 *
 * Created on October 18, 2001, 1:02 AM
 */

/**
 * Logging utility class for Gleam.
 */
public class Logger
{

    private static final int JUL_THRESHOLD_ALL = 1000;
    private static final int JUL_THRESHOLD_WARNING = 900;
    private static final int JUL_THRESHOLD_INFO = 800;
    private static final int JUL_THRESHOLD_CONFIG = 700;
    private static final int JUL_THRESHOLD_FINE = 500;
    private static final
    java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger("gleam");
    private static final Logger theLogger = new Logger();

    static {
        if (julLogger.getHandlers().length == 0) {
            julLogger.setUseParentHandlers(false);
            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(java.util.logging.Level.ALL);
            consoleHandler.setFormatter(new LogFormatter());
            julLogger.addHandler(consoleHandler);
            julLogger.setLevel(java.util.logging.Level.INFO);
        }
    }

    /**
     * The logging level
     * <pre><code>
     *  OFF     no output
     *  ERROR   a serious failure
     *  WARNING a potential problem
     *  INFO    significant messages
     *  CONFIG  configuration messages
     *  DEBUG   tracing information
     *  ALL     all messages
     * </code></pre>
     */
    public enum Level
    {
        OFF(6), ERROR(5), WARNING(4), INFO(3), CONFIG(2), DEBUG(1), ALL(0);

        private final int value;

        Level(int value)
        {
            this.value = value;
        }

        public static Level fromValue(int value)
        {
            for (Level level : values()) {
                if (level.getValue() == value) {
                    return level;
                }
            }
            return null;
        }

        public int getValue()
        {
            return value;
        }
    }

    /** Can't instantiate this class. */
    private Logger() {}

    /**
     * Get the Gleam logger.
     */
    public static Logger getLogger()
    {
        return theLogger;
    }

    /**
     * Sets current level. Message with level lower than this level will be discarded.
     *
     * @param level level value
     *
     * @see Level
     */
    public void setLevel(int level)
    {
        julLogger.setLevel(getJulLevel(level));
    }

    /**
     * Obtains a java.util.logging Level from an integer level value.
     *
     * @param level integer level value
     *
     * @return the corresponding Level
     */
    private static java.util.logging.Level getJulLevel(int level)
    {
        java.util.logging.Level n;
        if (level < 0) {
            level = 0;
        }
        switch (level) {
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
     * Sets current level. Message with level lower than this level will be discarded.
     *
     * @param level numeric level value
     *
     * @see Level
     */
    public void setLevel(Level level)
    {
        julLogger.setLevel(getJulLevel(level.getValue()));
    }

    /**
     * @return the current level as an int value
     */
    public Level getLevel()
    {
        return Level.fromValue(getLevelValue());
    }

    /**
     * @return the current level as an int value
     */
    public int getLevelValue()
    {
        return getLevelValue(julLogger.getLevel());
    }

    /**
     * Obtains a Level from an integer level value.
     *
     * @param level the current level
     *
     * @return the corresponding integer level value
     */
    private static int getLevelValue(java.util.logging.Level level)
    {
        final int n;

        if (level.intValue() == Integer.MAX_VALUE) {
            n = Level.OFF.getValue();
        }
        else if (level.intValue() >= JUL_THRESHOLD_ALL) {
            n = Level.ERROR.getValue();
        }
        else if (level.intValue() >= JUL_THRESHOLD_WARNING) {
            n = Level.WARNING.getValue();
        }
        else if (level.intValue() >= JUL_THRESHOLD_INFO) {
            n = Level.INFO.getValue();
        }
        else if (level.intValue() >= JUL_THRESHOLD_CONFIG) {
            n = Level.CONFIG.getValue();
        }
        else if (level.intValue() >= JUL_THRESHOLD_FINE) {
            n = Level.DEBUG.getValue();
        }
        else {
            n = Level.ALL.getValue();
        }
        return n;
    }

    /**
     * Logs a message.
     */
    public void log(int level, String message)
    {
        julLog(getJulLevel(level), message);
    }

    /**
     * Logs a message.
     */
    public void log(Level level, String message)
    {
        julLog(getJulLevel(level.getValue()), message);
    }

    /**
     * Logs a message.
     */
    public void log(Level level, Supplier<String> msgSupplier)
    {
        julLogger.log(getJulLevel(level.getValue()), msgSupplier);
    }

    /**
     * Logs a message and an Entity.
     */
    public void log(int level, String message, Entity obj)
    {
        julLog(getJulLevel(level), message, obj);
    }

    /**
     * Logs a message and an Entity.
     */
    public void log(Level level, String message, Entity obj)
    {
        julLog(getJulLevel(level.getValue()), message, obj);
    }

    private static void julLog(java.util.logging.Level level, String message)
    {
        julLogger.log(level, String.valueOf(message));
    }

    private static void julLog(java.util.logging.Level level, String message,
                               Entity obj)
    {
        julLogger.log(level, () -> String.format("%s %s", message, obj));
    }

    private static void julLog(java.util.logging.Level level, String message,
                               Throwable ex)
    {
        julLogger.log(level, String.valueOf(message), ex);
    }

    private static void julLog(java.util.logging.Level level, Throwable ex)
    {
        julLogger.log(level, String.valueOf(ex.getMessage()), ex);
    }

    /**
     * Logs a message at DEBUG level.
     *
     * @param message the message to log
     */
    public void debug(String message)
    {
        java.util.logging.Level level = java.util.logging.Level.FINE;
        julLog(level, message);
    }

    /**
     * Logs a message and an Entity at DEBUG level.
     *
     * @param message the message to log
     * @param obj     the Entity to log
     */
    public void debug(String message, Entity obj)
    {
        java.util.logging.Level level = java.util.logging.Level.FINE;
        julLog(level, message, obj);
    }

    /**
     * Logs a message at CONFIG level.
     *
     * @param message the message to log
     */
    public void config(String message)
    {
        julLog(java.util.logging.Level.CONFIG, message);
    }

    /**
     * Logs a message and an Entity at CONFIG level.
     *
     * @param message the message to log
     * @param obj     the Entity to log
     */
    public void config(String message, Entity obj)
    {
        julLog(java.util.logging.Level.CONFIG, message, obj);
    }

    /**
     * Logs a message at INFO level.
     *
     * @param message the message to log
     */
    public void info(String message)
    {
        julLog(java.util.logging.Level.INFO, message);
    }

    /**
     * Logs a message and an Entity at INFO level.
     *
     * @param message the message to log
     * @param obj     the Entity to log
     */
    public void info(String message, Entity obj)
    {
        julLog(java.util.logging.Level.INFO, message, obj);
    }

    /**
     * Logs a message at WARNING level.
     *
     * @param message the message to log
     */
    public void warning(String message)
    {
        julLog(java.util.logging.Level.WARNING, message);
    }

    /**
     * Logs a message and an Entity at WARNING level.
     *
     * @param message the message to log
     * @param obj     the Entity to log
     */
    public void warning(String message, Entity obj)
    {
        julLog(java.util.logging.Level.WARNING, message, obj);
    }

    /**
     * Logs a Throwable at WARNING level.
     *
     * @param ex Throwable to log
     */
    public void warning(Throwable ex)
    {
        java.util.logging.Level level = java.util.logging.Level.WARNING;
        julLog(level, ex);
    }

    /**
     * Logs a message and a Throwable at WARNING level.
     *
     * @param message the message to log
     * @param ex      Throwable to log
     */
    public void warning(String message, Throwable ex)
    {
        java.util.logging.Level level = java.util.logging.Level.WARNING;
        julLog(level, message, ex);
    }

    /**
     * Logs a message at SEVERE level.
     *
     * @param message the message to log
     */
    public void severe(String message)
    {
        julLog(java.util.logging.Level.SEVERE, message);
    }

    /**
     * Logs a message and an Entity at SEVERE level.
     *
     * @param message the message to log
     * @param obj     the Entity to log
     */
    public void severe(String message, Entity obj)
    {
        julLog(java.util.logging.Level.SEVERE, message, obj);
    }

    /**
     * Logs a Throwable at SEVERE level.
     *
     * @param ex Throwable to log
     */
    public void severe(Throwable ex)
    {
        julLog(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
    }

    /**
     * Logs a message and a Throwable at SEVERE level.
     *
     * @param message the message to log
     * @param ex      Throwable to log
     */
    public void severe(String message, Throwable ex)
    {
        julLog(java.util.logging.Level.SEVERE, message, ex);
    }

    // custom formatter

    private static class LogFormatter extends Formatter
    {

        private final DateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS Z");

        public String format(LogRecord logRecord)
        {
            getCallerInfo(logRecord);
            logRecord.setMessage(logRecord.getMessage().replace("\n", "\\\\n"));
            StringBuilder builder = new StringBuilder(1000);
            builder.append(df.format(new Date(logRecord.getMillis())))
                   .append(" - ");
            builder.append("[")
                   .append(logRecord.getSourceClassName())
                   .append(".");
            builder.append(logRecord.getSourceMethodName()).append("] - ");
            builder.append("[")
                   .append(Level.fromValue(getLevelValue(logRecord.getLevel())))
                   .append("] - ");
            builder.append(formatMessage(logRecord));
            builder.append("\n");
            return builder.toString();
        }

        private void getCallerInfo(LogRecord logRecord)
        {
            boolean loggerFound = false;
            StackTraceElement[] stackTrace = Thread.currentThread()
                                                   .getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (stackTraceElement.getClassName()
                                     .equals(Logger.class.getName())) {
                    loggerFound = true;
                }
                else if (loggerFound) {
                    logRecord.setSourceClassName(stackTraceElement.getClassName());
                    logRecord.setSourceMethodName(stackTraceElement.getMethodName());
                    break;
                }
            }
        }
    }
}
