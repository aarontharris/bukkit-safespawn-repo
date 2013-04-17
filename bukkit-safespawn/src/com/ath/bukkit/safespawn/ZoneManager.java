package com.ath.bukkit.safespawn;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

public class ZoneManager {

	private SafeSpawnPlugin plugin;


	private Set<Zone> allZones;

	public ZoneManager( SafeSpawnPlugin plugin ) {
		this.plugin = plugin;
		allZones = new HashSet<Zone>();
	}

	public void initializeFromConfig() {
		allZones.clear();
		for ( Zone zone : Zone.fromConfig( plugin ) ) {
			addZone( zone );
		}
	}

	public void logAllZones() {
		for ( Zone z : allZones ) {
			plugin.logLine( "logAllZones - zone: " + z.getName() );
		}
	}

	public void addZone( Zone zone ) {
		if ( zone != null && zone.getLocation() != null ) {
			allZones.add( zone );
		}
	}

	public void remZone( Zone zone ) {
		allZones.remove( zone );
	}

	public Set<Zone> findZones( Location l ) {
		return new HashSet<Zone>( allZones ); // FIXME: make this better
	}

}
