package log;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Utility class used to generate Log4j logger.
 * 
 * We can generate logs in a text file or an html file.
 * 
 * @author Tianxiao.Liu@u-cergy.fr & Yang Mattew
 */
public class LoggerUtility {
	private static final File TEXT_LOG_CONFIG = new File("src/log/log4j-text.properties");
	private static final File HTML_LOG_CONFIG = new File("src/log/log4j-html.properties");

	public static Logger getLogger(Class<?> logClass, String logFileType) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		if (logFileType.equals("text")) {
			context.setConfigLocation(TEXT_LOG_CONFIG.toURI());
		} else if (logFileType.equals("html")) {
			context.setConfigLocation(HTML_LOG_CONFIG.toURI());
		} else {
			throw new IllegalArgumentException("Unknown log file type !");
		}

		String className = logClass.getName();
		return LogManager.getLogger(className);
	}
}
