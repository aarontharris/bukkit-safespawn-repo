package com.ath.bukkit.safespawn.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;
import com.ath.bukkit.safespawn.data.BlockData;

public class BlockEventHandler {

	public static void onBlockBreakEvent( SafeSpawn plugin, BlockBreakEvent event ) {
		try {
			Block block = event.getBlock();
			for ( Zone zone : plugin.getZoneManager().findZones( block.getLocation() ) ) {
				Player player = event.getPlayer();
				if ( zone.caresAbout( block.getLocation(), ZoneExclude.BLOCK_BREAK ) ) {
					if ( !ZoneExclude.BLOCK_BREAK.hasPermission( player, zone ) ) {
						event.setCancelled( true );
						player.sendMessage( "This is a no block breaking zone" );
						break;
					}
				}
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}

		// cleanup destroyed blocks
		try {
			plugin.getBlockStore().remove( event.getBlock() );
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}

	@EventHandler
	public static void onBlockPlaceEvent( SafeSpawn plugin, BlockPlaceEvent event ) {
		// TODO: ATH-P3 -- make me more efficient
		// P3 because there are unlikely to be many zones as long as they are only created within the config.yml
		// - - if I allow others to create zones within the game, then we really need to bump up the priority.
		// I should be able to hash down to a narrow list of zones based on a world or even a chunk?
		// maybe only get zones that care about the event:
		// - - EX: onBlockPlaceEvent, only get zones that exclude the BlockPlaceEvent

		// First see if the event gets cancelled in here
		try {
			Block block = event.getBlock();
			for ( Zone zone : plugin.getZoneManager().findZones( block.getLocation() ) ) {
				Player player = event.getPlayer();
				// if ( zone.isTouching( block.getLocation() ) && zone.hasExclude( ZoneExclude.BLOCK_PLACE ) ) {
				if ( zone.caresAbout( block.getLocation(), ZoneExclude.BLOCK_PLACE ) ) {
					// if ( !player.isPermissionSet( Const.PERM_blockplace ) && !player.isPermissionSet( Const.PERM_blockplace + "." + zone.getName() ) ) {
					if ( !ZoneExclude.BLOCK_PLACE.hasPermission( player, zone ) ) {
						event.setCancelled( true );
						player.sendMessage( "This is a no block placement protected zone" );
						break;
					}
				}
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}


		// if the event didn't get cancelled
		try {
			if ( !event.isCancelled() ) {
				Player player = event.getPlayer();

				// is the block magical
				if ( plugin.getPlayerStore().isCasting( player ) ) {
					BlockData data = plugin.getBlockStore().attainBlockData( event.getBlock() );
					data.setMagical( true );
					plugin.getBlockStore().saveBlockData( data );
				}
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}

		// always cancel the cast
		try {
			plugin.getPlayerStore().endCasting( event.getPlayer() );
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
	}
	// public static void onSignChangeEvent( SafeSpawnPlugin plugin, SignChangeEvent event ) {
	// Functions.teleport( plugin, event.getPlayer(), plugin.getSpawnLocation() );
	// }
}
