// Log.java
package SW;

import java.util.logging.FileHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * The Log class provides a custom logger configuration for the application.
 */
public class Log {

    /**
     * The logger instance for general logging.
     */
    public static final Logger logger = Logger.getLogger("General logger");

    /**
     * Initializes the custom logger configuration by removing the default console handler,
     * adding a file handler for writing logs to a file, and adding a console handler for logging to the console.
     * The log level is set to ALL.
     */
    public static void createLogger() {
        try {
            // Remove default console handler
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                }
            }

            logger.setLevel(Level.ALL);

            // Add FileHandler to log to last.log file
            FileHandler fileHandler = new FileHandler("last.log", 1000000, 1);
            fileHandler.setFormatter(new CustomOneLineFormatter());
            logger.addHandler(fileHandler);

            // Add ConsoleHandler to log to console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomOneLineFormatter());
            logger.addHandler(consoleHandler);


        } catch (Exception ignored) {
            // Log any exceptions during logger setup (ignored for simplicity)
        }
    }

    /**
     * Custom formatter for log records to display in a single line.
     */
    private static class CustomOneLineFormatter extends Formatter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        /**
         * Formats a log record into a single line with timestamp, log level, class, method, and message.
         *
         * @param record The log record to be formatted.
         * @return The formatted log record as a String.
         */
        @Override
        public String format(LogRecord record) {
            return String.format("[%s] %s (%s:%s) - %s%n",
                    dateFormat.format(new Date(record.getMillis())),
                    record.getLevel(),
                    record.getSourceClassName(),
                    record.getSourceMethodName(),
                    record.getMessage()
            );
        }
    }
}
