package com.menumitra.utilityclass;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class LogUtils 
{


    // Log4j Logger instance - using the root logger for the package
    private static final Logger log = Logger.getLogger("com.menumitra");
    private static final String LOG_FILE_PATH = "src/test/resources/log/log4j.log";
    private static final String LOG4J_CONFIG_PATH = "log4j.xml";
    
    // Static initializer block to ensure Log4j is configured when class is loaded
    static {
        try {
            // Configure Log4j with XML
            DOMConfigurator.configure(LOG4J_CONFIG_PATH);
            System.out.println("Log4j configured from static initializer");
        } catch (Exception e) {
            System.err.println("Error configuring Log4j in static initializer: " + e.getMessage());
        }
    }

    /**
     * Initializes Log4j configuration and appends blank lines for session
     * separation.
     */
    public static void initializeLogger() 
    {
        try {
            // Add blank lines to separate logs for the new session
            addBlankLinesToLogFile(LOG_FILE_PATH, 3);
            log.info("=== Logger initialized with session markers ===");
        } catch (Exception e) {
            System.err.println("Error initializing Log4j: " + e.getMessage());
        }
    }

    /**
     * Appends blank lines to the log file for session separation.
     *
     * @param logFilePath Path to the log file.
     * @param lineCount   Number of blank lines to append.
     * @throws CustomExceptions
     * @throws IOException
     */
    private static void addBlankLinesToLogFile(String logFilePath, int lineCount) throws customException, IOException {
        try {
            FileWriter writer = new FileWriter(logFilePath, true);
            for (int i = 0; i < lineCount; i++) {
                writer.write(System.lineSeparator());
            }
            writer.write("===== MenuMitra Execution Start (" + new Date() + ") =====" + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            throw new customException("Error writing session marker to log file: " + e.getMessage());
        }
    }

    /**
     * Logs the start of a test case execution.
     *
     * @param testName Name of the test case.
     */
    public static void startTest(String testName) {
        log.info("========== " + testName + " START ==========");
    }

    /**
     * Logs the end of a test case execution.
     *
     * @param testName Name of the test case.
     */
    public static void endTest(String testName) {
        log.info("========== " + testName + " END ==========");
    }

    /**
     * Logs informational messages during execution.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        // Get the calling class to use its logger
        String callerClassName = getCallerClassName();
        Logger callerLogger = Logger.getLogger(callerClassName);
        callerLogger.info(message);
    }

    /**
     * Logs warning messages during execution.
     *
     * @param message The message to log.
     */
    public static void warn(String message) {
        // Get the calling class to use its logger
        String callerClassName = getCallerClassName();
        Logger callerLogger = Logger.getLogger(callerClassName);
        callerLogger.warn(message);
    }

    /**
     * Logs error messages encountered during execution.
     *
     * @param message The message to log.
     */
    public static void error(String message) {
        // Get the calling class to use its logger
        String callerClassName = getCallerClassName();
        Logger callerLogger = Logger.getLogger(callerClassName);
        callerLogger.error(message);
    }

    /**
     * Logs fatal error messages during execution.
     *
     * @param message The message to log.
     */
    public static void fatal(String message) {
        // Get the calling class to use its logger
        String callerClassName = getCallerClassName();
        Logger callerLogger = Logger.getLogger(callerClassName);
        callerLogger.fatal(message);
    }

    /**
     * Logs debug messages for detailed information during execution.
     *
     * @param message The message to log.
     */
    public static void debug(String message) {
        // Get the calling class to use its logger
        String callerClassName = getCallerClassName();
        Logger callerLogger = Logger.getLogger(callerClassName);
        callerLogger.debug(message);
    }
    
    /**
     * Gets the class name of the method that called the logging method.
     * 
     * @return The name of the calling class
     */
    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // Index 0 is getStackTrace
        // Index 1 is getCallerClassName
        // Index 2 is the logging method (info, error, etc.)
        // Index 3 is the caller of the logging method
        if (stackTrace.length >= 4) {
            return stackTrace[3].getClassName();
        }
        // Default to our package if we can't determine caller
        return "com.menumitra";
    }
}
