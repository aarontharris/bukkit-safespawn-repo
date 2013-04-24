package com.ath.bukkit.safespawn.event;

import org.bukkit.event.entity.EntityExplodeEvent;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;

public class EntityEventHandler {

	public static void onEntityExplode( SafeSpawn plugin, EntityExplodeEvent event ) {
		try {
			for ( Zone zone : plugin.getZoneManager().findZones( event.getLocation() ) ) {
				if ( zone.caresAbout( ZoneExclude.BLOCK_BREAK ) ) {
					event.setCancelled( true );
				}
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}
}
