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

		// BADDIE BLOCKER
		for ( Zone zone : plugin.getZoneManager().findZones( event.getLocation() ) ) {
			if ( zone.caresAbout( event.getLocation() ) ) {
				if ( Functions.isBaddie( entityType ) ) {
					if ( zone.caresAbout( ZoneExclude.BADDIE_SPAWN ) ) {
						event.setCancelled( true );
						break;
					}
				}
			}
		}

	}
}
