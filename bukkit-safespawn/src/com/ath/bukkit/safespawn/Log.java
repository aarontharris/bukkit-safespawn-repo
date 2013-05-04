package com.ath.bukkit.safespawn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Log {

	private static Log self = new Log();
	private Logger logger;

	public static void init( JavaPlugin plugin ) {
		self.logger = plugin.getLogger();
	}

	public static void line() {
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
			e.printStackTrace();
		}
	}

	public static void error( Exception e ) {
		try {
			self.logger.log( Level.SEVERE, e.getMessage() );
			for ( StackTraceElement el : e.getStackTrace() ) {
				self.logger.log( Level.SEVERE, el.getFileName() + ":" + el.getLineNumber() );
			}
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
}
