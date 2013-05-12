package com.ath.bukkit.safespawn.ctrl.cdc;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.SafeSpawn;

public class CDCController {

	public static void onEntityDamagedByEntity( SafeSpawn plugin, EntityDamageByEntityEvent event ) {

		// We don't care if PVP is enabled or not
		// if ( event.isCancelled() ) { return; }

		// Attackee must be a player
		if ( EntityType.PLAYER.equals( event.getEntityType() ) ) {

			// Plague
			if ( F.canCarryPlague( event.getDamager().getType() ) ) {
				// FIXME: CDC
			}

		}
	}

}
