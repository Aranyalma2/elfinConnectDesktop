package SW;

import java.util.logging.FileHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log {

    public static final Logger logger = Logger.getLogger("General logger");

    public static void createLogger() {
        try {

            //Remove default logger handler
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof ConsoleHandler) {
                    rootLogger.removeHandler(handler);
                }
            }

            // Add FileHandler to log to last.log file
            FileHandler fileHandler = new FileHandler("last.log");
            fileHandler.setFormatter(new CustomOneLineFormatter());
            logger.addHandler(fileHandler);

            // Add ConsoleHandler to log to console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomOneLineFormatter());
            logger.addHandler(consoleHandler);
        } catch (Exception ignored) {
        }
    }

    private static class CustomOneLineFormatter extends Formatter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

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

