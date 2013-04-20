package com.ath.bukkit.safespawn.event;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.SafeSpawn;

public class ZoneEventHandler {

	public static void onCreatureSpawnEvent( SafeSpawn plugin, CreatureSpawnEvent event ) {
		EntityType entityType = event.getEntityType();
		if ( Functions.isBaddie( entityType ) ) {
			event.setCancelled( true );
		}
	}

}
