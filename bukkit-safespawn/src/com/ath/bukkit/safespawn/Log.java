package com.ath.bukkit.safespawn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Log {

	private static Log self = new Log();
	private Logger logger = null;

	public static void init( JavaPlugin plugin ) {
		self.logger = plugin.getLogger();
	}

	public static void line() {
		line( "" );
	}

	public static void line( String format, Object... args ) {
		try {
			Throwable t = new Throwable();
			StackTraceElement el = t.getStackTrace()[1];
			if ( format == null || format.isEmpty() ) {
				self.logger.info( String.format( "%s: %s", el.getFileName(), el.getLineNumber() ) );
			} else {
				self.logger.info( String.format( "%s: %s: %s", String.format( format, args ), el.getFileName(), el.getLineNumber() ) );
			}
		} catch ( Exception e ) {
			System.out.printf( format + "\n", args );
		}
	}

	public static void error( Exception e ) {
		try {
			self.logger.log( Level.SEVERE, e.getClass().getName() + ": " + e.getMessage() );
			for ( StackTraceElement el : e.getStackTrace() ) {
				self.logger.log( Level.SEVERE, "  at " + el.getClassName() + "." + el.getMethodName() + "(" + el.getFileName() + ":" + el.getLineNumber() + ")" );
			}
			if ( e.getCause() != null ) {
				Throwable c = e.getCause();
				self.logger.log( Level.SEVERE, "Caused by: " + c.getClass().getName() + ": " + e.getMessage() );
				for ( StackTraceElement el : c.getStackTrace() ) {
					self.logger.log( Level.SEVERE, "  at " + el.getClassName() + "." + el.getMethodName() + "(" + el.getFileName() + ":" + el.getLineNumber() + ")" );
				}
			}
		} catch ( Exception ex ) {
			e.printStackTrace();
		}
	}
}
