package com.ath.bukkit.safespawn;

/**
 * <pre>
 * <code>
 * TestTimer t = new TestTimer();
 * t.start();
 * // do work
 * t.stop();
 * System.out.prinln( t.elapsedPretty() );
 * t.reset(); // if you wish to reuse
 * </code>
 * </pre>
 */
public class TestTimer {

	private long startTime;
	private long stopTime;
	private int lap = 0;

	public TestTimer() {
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void lap() {
		lap++ ;
	}

	public void stop() {
		stopTime = System.currentTimeMillis();
	}

	public void reset() {
		startTime = 0;
		stopTime = 0;
	}

	public long elapsed() {
		return stopTime - startTime;
	}

	public double avgLap() {
		return elapsed() / lap;
	}

	public String elaspedPretty() {
		return elapsedPretty( elapsed() );
	}

	/** @param elapsed - milliseconds */
	public static String elapsedPretty( long elapsed ) {
		long sec = ( elapsed / 1000 );
		long mills = ( elapsed % 1000 );

		if ( sec > 60 ) {
			long min = sec / 60;
			sec -= min * 60;
			return String.format( "%sm %ss %sms", min, sec, mills );
		} else if ( sec > 0 ) {
			return String.format( "%ss %sms", sec, mills );
		}
		return String.format( "%sms", mills );
	}

	public static void main( String args[] ) {
		Log.line( "TIME: %s", elapsedPretty( 100 ) );
		Log.line( "TIME: %s", elapsedPretty( 1000 ) );
		Log.line( "TIME: %s", elapsedPretty( 1100 ) );
		Log.line( "TIME: %s", elapsedPretty( 10100 ) );
		Log.line( "TIME: %s", elapsedPretty( 61100 ) );
	}
}
