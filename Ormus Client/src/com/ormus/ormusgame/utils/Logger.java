package com.ormus.ormusgame.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class Logger {
	private static PrintWriter output;
	private static Calendar calendar = Calendar.getInstance();
	
	private static short loggerSensitivity = 1;
	public static short DEBUG = 0;
	public static short INFO = 1;
	public static short ERROR = 2;
	
	static {
		try {
			output = new PrintWriter(new FileWriter("log.txt", true), true);
			output.println("--------------------------------");
			output.println("--        NEW SESSION         --");
			output.println("--------------------------------");
		} catch (IOException e) {
			log(ERROR, "could not create log file! (" + e.getMessage() + ")");
		}	
	}
	
	public static void setSensitivity(short sensitivity) {
		if (sensitivity < 0 || sensitivity > 2) {
			log(ERROR, "invalid sensitivity:" + sensitivity);
		} else {
			loggerSensitivity = sensitivity;
		}
	}
	
	public static void log(short type, String message) {
		String typeString = "";
		
		switch (type) {
		case 0: typeString = "DEBUG"; break;
		case 1: typeString = "INFO"; break;
		case 2: typeString = "ERROR"; break;
		}
		
		String loggedMessage = "[" + calendar.getTime() + "] " + typeString + ": " + message;
		
		System.out.println(loggedMessage);
		if (loggerSensitivity <= type) output.println(loggedMessage);
	}	
	
}
