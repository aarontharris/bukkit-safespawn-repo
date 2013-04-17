package com.ath.bukkit.safespawn.event;

import java.util.Date;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.SafeSpawnPlugin;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;
import com.ath.bukkit.safespawn.data.PlayerDao;
import com.ath.bukkit.safespawn.data.PlayerData;

public class PlayerEventHandler {

	public static void onPlayerJoin( SafeSpawnPlugin plugin, PlayerJoinEvent event ) {
		Player player = event.getPlayer();
		plugin.getPlayerManager().cachePlayer( player );
		player.sendMessage( plugin.getConfig().getString( Const.MSG_welcome_message ) );

		// configure first time user data
		try {
			PlayerDao dao = plugin.getPlayerDao();
			PlayerData data = dao.readPlayerData( player );
			if ( data.getTimesLoggedIn() == 0 ) {
				data = new PlayerData();
				data.setName( player.getName() );
				data.setFirstLogin( new Date() );
			}
			data.setLastLogin( new Date() );
			data.setTimesLoggedIn( data.getTimesLoggedIn() + 1 );
			dao.writePlayerData( data, player );
		} catch ( Exception e ) {
			SafeSpawnPlugin.logError( e );
		}

		// Teleport first time users to spawn area
		{
			player.setBedSpawnLocation( plugin.getSpawnLocation().clone() );
			Functions.teleport( plugin, player, plugin.getSpawnLocation() );
		}
	}

	public static void onPlayerLeave( SafeSpawnPlugin plugin, PlayerQuitEvent event ) {
		// TODO: make sure when a player is banned or kicked that it still calls the PlayerQuitEvent
		Player player = event.getPlayer();
		plugin.getPlayerManager().removePlayerFromCache( player );
	}

	public static void onEntityDamagedByEntity( SafeSpawnPlugin plugin, EntityDamageByEntityEvent event ) {
		if ( event.getEntityType() == EntityType.PLAYER ) {
			Entity entity = event.getEntity();
			Player player = plugin.getPlayerManager().getActivePlayerByEntityId( entity.getEntityId() );
			if ( player != null ) {
				for ( Zone zone : plugin.getZoneManager().findZones( entity.getLocation() ) ) {
					if ( zone.caresAbout( entity.getLocation(), ZoneExclude.PLAYER_DMG_FROM_ENTITY ) ) {
						if ( !ZoneExclude.PLAYER_DMG_FROM_ENTITY.hasPermission( player, zone ) ) {
							event.setCancelled( true );
						}
					}
				}
			}
		}
	}
}