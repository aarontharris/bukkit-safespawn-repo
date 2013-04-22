package com.ath.bukkit.safespawn.data;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.avaje.ebean.EbeanServer;

public class PlayerStore {

	private EbeanServer database;

	public PlayerStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		SafeSpawn.logError( e );
	}
	
}
