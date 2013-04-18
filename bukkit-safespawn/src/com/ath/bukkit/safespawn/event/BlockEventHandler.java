package com.ath.bukkit.safespawn.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ath.bukkit.safespawn.SafeSpawnPlugin;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;

public class BlockEventHandler {

	public static void onBlockBreakEvent( SafeSpawnPlugin plugin, BlockBreakEvent event ) {
		Block block = event.getBlock();
		for ( Zone zone : plugin.getZoneManager().findZones( block.getLocation() ) ) {
			Player player = event.getPlayer();
			if ( zone.caresAbout( block.getLocation(), ZoneExclude.BLOCK_BREAK ) ) {
				if ( !ZoneExclude.BLOCK_BREAK.hasPermission( player, zone ) ) {
					event.setCancelled( true );
					player.sendMessage( "This is a no block breaking zone" );
				}
			}
		}
	}

	@EventHandler
	public static void onBlockPlaceEvent( SafeSpawnPlugin plugin, BlockPlaceEvent event ) {
		// TODO: ATH-P3 -- make me more efficient
		// P3 because there are unlikely to be many zones as long as they are only created within the config.yml
		// - - if I allow others to create zones within the game, then we really need to bump up the priority.
		// I should be able to hash down to a narrow list of zones based on a world or even a chunk?
		// maybe only get zones that care about the event:
		// - - EX: onBlockPlaceEvent, only get zones that exclude the BlockPlaceEvent
		Block block = event.getBlock();
		for ( Zone zone : plugin.getZoneManager().findZones( block.getLocation() ) ) {
			Player player = event.getPlayer();
			// if ( zone.isTouching( block.getLocation() ) && zone.hasExclude( ZoneExclude.BLOCK_PLACE ) ) {
			if ( zone.caresAbout( block.getLocation(), ZoneExclude.BLOCK_PLACE ) ) {
				// if ( !player.isPermissionSet( Const.PERM_blockplace ) && !player.isPermissionSet( Const.PERM_blockplace + "." + zone.getName() ) ) {
				if ( !ZoneExclude.BLOCK_PLACE.hasPermission( player, zone ) ) {
					event.setCancelled( true );
					player.sendMessage( "This is a no block placement protected zone" );
				}
			}
		}
	}

	// public static void onSignChangeEvent( SafeSpawnPlugin plugin, SignChangeEvent event ) {
	// Functions.teleport( plugin, event.getPlayer(), plugin.getSpawnLocation() );
	// }
}
