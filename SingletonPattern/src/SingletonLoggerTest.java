import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

public class SingletonLoggerTest {

	public static void main(String[] args) throws Exception {
		consolerLoggerTest();
		fileLoggerTest();	
	}
	
	static void consolerLoggerTest() throws Exception {
		System.out.println("consolerLoggerTest() is started...");
		AbstractLogger logger = ConsoleLogger.getInstance();

		logger.log("First log");
		TimeUnit.SECONDS.sleep(2);
		
		logger.log("Second log");
		TimeUnit.SECONDS.sleep(3);

		logger.log(logger);		
		System.out.println("consolerLoggerTest() is ended \n\n");
	}

	static void fileLoggerTest() throws Exception {
		System.out.println("fileLoggerTest() is started...");
		AbstractLogger logger = FileLogger.getInstance("log.txt");

		logger.log("First log");
		TimeUnit.SECONDS.sleep(2);
		
		logger.log("Second log");
		TimeUnit.SECONDS.sleep(3);

		logger.log(logger);
		
		logger.end();
		System.out.println("fileLoggerTest() is ended");
	}
	
}

abstract class AbstractLogger {
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	abstract void log(Object o);
	
	void end() { }
	
}

final class ConsoleLogger extends AbstractLogger {
	private static AbstractLogger instance;
	
	private ConsoleLogger() {
		
	}
	
	synchronized static AbstractLogger getInstance() {
		if (instance == null) {
			instance = new ConsoleLogger();
		}
		
		return instance;
	}

	@Override
	synchronized void log(Object o) {
		System.out.println(o + " (Time = " + dateFormat.format(new Date()) + ")");
	}
	
	@Override
	public String toString() {
		return "ConsoleLogger";
	}
	
}

final class FileLogger extends AbstractLogger {
	private static OutputStreamWriter writer;
	private static AbstractLogger instance;
	
	private FileLogger(String fileName) {
		try {
			writer = new OutputStreamWriter(new FileOutputStream(fileName));
		} 
		catch(Exception e) {
			System.out.println("Log file creation error : " + e);
		}
	}
	
	synchronized static AbstractLogger getInstance() {
		if (instance == null) {
			instance = new FileLogger("log.txt");
		}
		
		return instance;
	}
	
	synchronized static AbstractLogger getInstance(String fileName) {
		if (instance == null) {
			instance = new FileLogger(fileName);
		}
		
		return instance;
	}

	@Override
	synchronized void log(Object o) {
		try {
			writer.append(o + " (Time = " + dateFormat.format(new Date()) + ") \n");
			writer.flush();
		} 
		catch(Exception e) {
			System.out.println("Logging error : " + e);
		}
	}
	
	@Override
	synchronized void end() { 
		try {
			writer.close();
			
			writer = null;
			instance = null;
		} 
		catch(Exception e) {
			System.out.println("Logging error : " + e);
		}		
	}
	
	@Override
	public String toString() {
		return "FileLogger";
	}
	
}

