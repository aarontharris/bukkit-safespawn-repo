package com.ath.bukkit.safespawn.ctrl.pvp;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;

public class PVPController {

	// FIXME: handle config.yml property pvp-mode: day|night|on|off
	// FIXME: allow for whitelisting(always) and blacklisting(never) players from pvp
	public static void onEntityDamagedByEntity( SafeSpawn plugin, EntityDamageByEntityEvent event ) {
		try {
			// skip if another handler has already cancelled the event
			if ( event.isCancelled() ) {
				return;
			}

			// only consider Player vs Player
			if ( EntityType.PLAYER.equals( event.getEntityType() ) && EntityType.PLAYER.equals( event.getDamager().getType() ) ) {
				World w = event.getEntity().getLocation().getWorld();

				// fail if sun is up
				if ( F.isSunUp( w ) ) {
					// send a random fail message
					if ( event.getDamager() instanceof Player ) {
						( (Player) event.getDamager() ).sendMessage( F.randomMessage(
								"You can't do that yet.",
								"Wait until it gets darker.",
								"Have patience...",
								"The sun is up.",
								"Maybe if it were night time."
								) );
					}
					event.setCancelled( true );
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

}
