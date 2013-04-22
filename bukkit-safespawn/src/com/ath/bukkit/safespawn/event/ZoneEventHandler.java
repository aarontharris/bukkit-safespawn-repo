package com.ath.bukkit.safespawn.event;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;

public class ZoneEventHandler {

	public static void onCreatureSpawnEvent( SafeSpawn plugin, CreatureSpawnEvent event ) {
		EntityType entityType = event.getEntityType();

		SafeSpawn.logLine( "creatureSpawn" );

		for ( Zone zone : plugin.getZoneManager().findZones( event.getLocation() ) ) {
			SafeSpawn.logLine( "creatureSpawn - " + zone );
			if ( zone.caresAbout( event.getLocation() ) ) {
				SafeSpawn.logLine( "creatureSpawn - cares about location" );
				if ( Functions.isBaddie( entityType ) ) {
					SafeSpawn.logLine( "creatureSpawn - is baddie" );
					if ( zone.caresAbout( ZoneExclude.BADDIE_SPAWN ) ) {
						SafeSpawn.logLine( "creatureSpawn - cares about baddie" );
						event.setCancelled( true );
					}
				}
			}
		}

	}
}
