package com.ath.bukkit.safespawn.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;

public class BlockEventHandler {

	public static void onBlockBreakEvent( SafeSpawn plugin, BlockBreakEvent event ) {
		Log.line( "%s.break @ %s - chunk = %s,%s",
				event.getPlayer().getName(),
				Functions.toString( event.getBlock().getLocation() ),
				event.getBlock().getChunk().getX(),
				event.getBlock().getChunk().getZ()
				);

		// Zone protection
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
			Log.error( e );
		}

		// Ownership protection
		try {
			if ( !event.isCancelled() ) {
				Block b = event.getBlock();
				Player player = event.getPlayer();
				Block ctrl = Functions.isOwnedBlock( b.getLocation(), b.getType() );
				if ( ctrl != null ) {
					if ( Functions.canUserAccessBlock( ctrl.getLocation(), ctrl.getType(), event.getPlayer() ) ) {
						player.sendMessage( "You just destroyed a magic block" );
					} else {
						player.sendMessage( "This block is protected, talk to the owner" );
						event.setCancelled( true );
					}
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}

		// cleanup destroyed blocks
		try {
			if ( !event.isCancelled() ) {
				plugin.getBlockStore().remove( event.getBlock() );
			}
		} catch ( Exception e ) {
			Log.error( e );
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

		// FIXME: even better -- when a block is created, destroyed, restored or generated from the world, maintain a ref to BlockData

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
			Log.error( e );
		}
	}

	// public static void onSignChangeEvent( SafeSpawnPlugin plugin, SignChangeEvent event ) {
	// Functions.teleport( plugin, event.getPlayer(), plugin.getSpawnLocation() );
	// }

	public static void onBlockFadeEvent( SafeSpawn plugin, BlockFadeEvent event ) {
		Log.line( "block fade @ %s", Functions.toString( event.getBlock() ) );

		// Ownership protection
		try {
			if ( !event.isCancelled() ) {
				Block b = event.getBlock();
				Block ctrl = Functions.isOwnedBlock( b.getLocation(), b.getType() );
				if ( ctrl != null ) {
					event.setCancelled( true );
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
