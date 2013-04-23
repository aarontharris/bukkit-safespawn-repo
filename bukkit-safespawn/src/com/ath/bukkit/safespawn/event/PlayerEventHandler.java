package com.ath.bukkit.safespawn.event;

import java.util.Date;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;
import com.ath.bukkit.safespawn.data.PlayerData;
import com.ath.bukkit.safespawn.magic.sign.MagicSign;
import com.ath.bukkit.safespawn.magic.sign.SignReader;

public class PlayerEventHandler {

	public static void onPlayerJoin( SafeSpawn plugin, PlayerJoinEvent event ) {
		try {
			Player player = event.getPlayer();
			plugin.getPlayerManager().cachePlayer( player );
			player.sendMessage( plugin.getConfig().getString( Const.MSG_welcome_message ) );

			PlayerData data = plugin.getPlayerStore().getPlayerData( player );

			// User Maintenance
			{
				// New User
				if ( data == null ) {
					SafeSpawn.logLine( "creating new user: " + player.getName() );
					data = PlayerData.newPlayerData( player );
					data.setTimesLoggedIn( 1 );

					// Teleport first time users to spawn area
					{
						player.setBedSpawnLocation( plugin.getSpawnLocation().clone(), true );
						Functions.teleport( plugin, player, plugin.getSpawnLocation() );
					}
				}

				// Returning users
				else {
					SafeSpawn.logLine( "returning user: " + player.getName() );
					data.setLastLogin( new Date() );
					data.setTimesLoggedIn( data.getTimesLoggedIn() + 1 );
				}

				// Save
				plugin.getPlayerStore().savePlayerData( data );
			}

		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}

	public static void onPlayerLeave( SafeSpawn plugin, PlayerQuitEvent event ) {
		// TODO: make sure when a player is banned or kicked that it still calls the PlayerQuitEvent
		try {
			Player player = event.getPlayer();
			plugin.getPlayerManager().removePlayerFromCache( player );
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}

	public static void onEntityDamagedByEntity( SafeSpawn plugin, EntityDamageByEntityEvent event ) {
		try {
			if ( event.getEntityType() == EntityType.PLAYER ) {
				Entity entity = event.getEntity();
				Player player = plugin.getPlayerManager().getActivePlayerByEntityId( entity.getEntityId() );
				if ( player != null ) {
					for ( Zone zone : plugin.getZoneManager().findZones( entity.getLocation() ) ) {
						if ( zone.caresAbout( entity.getLocation(), ZoneExclude.PLAYER_DMG_FROM_ENTITY ) ) {
							if ( !ZoneExclude.PLAYER_DMG_FROM_ENTITY.hasPermission( player, zone ) ) {
								event.setCancelled( true );
								break;
							}
						}
					}
				}
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}

	public static void onPlayerInteractEvent( SafeSpawn plugin, PlayerInteractEvent event ) {
		try {
			if ( event.getAction().equals( Action.LEFT_CLICK_BLOCK ) ) {
				Block block = event.getClickedBlock();

				if ( event.getPlayer().getName().equals( "angryBits" ) ) {
					SafeSpawn.logLine( "player clicked: " + block.getType() );
				}

				// WALL SIGN
				if ( block.getType().equals( Material.WALL_SIGN ) || block.getType().equals( Material.SIGN_POST ) ) { // || block.getType().equals( Material.SIGN ) ) {
					if ( event.getPlayer().hasPermission( Const.PERM_magic_sign ) ) {
						BlockState state = block.getState();
						if ( state instanceof Sign ) {
							MagicSign sign = SignReader.readSign( (Sign) state );
							if ( sign.activateSign( (Sign) state, event ) ) {
								// TODO: send a message?
							}
						}
					}
				}

			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}
}
