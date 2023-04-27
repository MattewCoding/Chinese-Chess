package log;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import outOfGameScreens.ScreenParam;

/**
 * Utility class used to generate Log4j logger.
 * 
 * We can generate logs in a text file or an html file.
 * 
 * How to use log4j2: https://www.youtube.com/playlist?list=PLL34mf651faNrQbmeK2XigJ68fLAj9buv
 * 
 * @author Tianxiao.Liu@u-cergy.fr & Yang Mattew
 */
public class LoggerUtility {
	private static final char PATHSEP = ScreenParam.PATHSEP;
	private static final String FILELOCATION = "."+PATHSEP+"src"+PATHSEP+"log"+PATHSEP;
	
	private static final File CONSOLE_LOG_CONFIG = new File(FILELOCATION+"log4j2-console.xml");
	private static final File TEXT_LOG_CONFIG = new File(FILELOCATION+"log4j2-text.xml");
	private static final File HTML_LOG_CONFIG = new File(FILELOCATION+"log4j2-html.xml");

	public static Logger getLogger(Class<?> logClass, String logFileType) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		if(logFileType.equals("console")) {
			context.setConfigLocation(CONSOLE_LOG_CONFIG.toURI());
	    } else if (logFileType.equals("text")) {
			context.setConfigLocation(TEXT_LOG_CONFIG.toURI());
		} else if (logFileType.equals("html")) {
			context.setConfigLocation(HTML_LOG_CONFIG.toURI());
		} else {
			throw new IllegalArgumentException("Unknown log file type !");
		}

		String className = logClass.getClass().getName();
		return LogManager.getLogger(className);
	}
}
