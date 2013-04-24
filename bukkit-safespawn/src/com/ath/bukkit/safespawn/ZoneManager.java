package com.ath.bukkit.safespawn;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

public class ZoneManager {

	private SafeSpawn plugin;


	private Set<Zone> allZones;

	public ZoneManager( SafeSpawn plugin ) {
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
			SafeSpawn.logLine( "logAllZones - zone: " + z.getName() );
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

	// TODO: can still be better
	// currently doing a radius check on all zones, find a way to hash down zones
	// also doing a new HashSet each time, maybe we can cache this
	/** Only returns zones that overlap the given location */
	public Set<Zone> findZones( Location l ) {
		HashSet<Zone> out = new HashSet<Zone>();
		for ( Zone zone : allZones ) {
			if ( zone.caresAbout( l ) ) {
				out.add( zone );
			}
		}
		return out;
	}

}
