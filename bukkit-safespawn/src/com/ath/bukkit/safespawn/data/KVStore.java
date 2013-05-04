package com.ath.bukkit.safespawn.data;

import java.util.Set;

import com.ath.bukkit.safespawn.Log;
import com.avaje.ebean.EbeanServer;

public class KVStore {

	private EbeanServer database;

	public KVStore( EbeanServer database ) {
		this.database = database;
	}

	private void logError( Exception e ) {
		Log.error( e );
	}

	private String findStringByString( String key ) {
		try {
			Set<SimpleKeyVal> vals = database.find( SimpleKeyVal.class ).where().ieq( SimpleKeyVal.KEY, key ).findSet();
			for ( SimpleKeyVal val : vals ) {
				return val.getValue();
			}
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private void putStringByString( String key, String value ) {
		try {
			Set<SimpleKeyVal> vals = database.find( SimpleKeyVal.class ).where().ieq( SimpleKeyVal.KEY, key ).findSet();
			if ( vals.size() > 0 ) {
				database.delete( vals );
			}
			SimpleKeyVal kv = new SimpleKeyVal( key, value );
			database.save( kv );
		} catch ( Exception e ) {
			logError( e );
		}
	}


}
