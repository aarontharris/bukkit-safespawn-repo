package com.ath.bukkit.safespawn.event;

import java.util.Date;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.Zone;
import com.ath.bukkit.safespawn.Zone.ZoneExclude;
import com.ath.bukkit.safespawn.data.BlockData;
import com.ath.bukkit.safespawn.data.Blocks;
import com.ath.bukkit.safespawn.data.PlayerData;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicCommand;
import com.ath.bukkit.safespawn.magic.sign.MagicSign;
import com.ath.bukkit.safespawn.magic.sign.SignReader;
import com.google.common.collect.Sets;

public class PlayerEventHandler {

	public static void onPlayerJoinEvent( SafeSpawn plugin, PlayerJoinEvent event ) {
		try {
			Player player = event.getPlayer();
			plugin.getPlayerManager().cachePlayer( player );
			player.sendMessage( plugin.getConfig().getString( Const.MSG_welcome_message ) );

			PlayerData data = PlayerData.get( player );

			// User Maintenance
			{
				// New User
				if ( data == null ) {
					Log.line( "creating new user: " + player.getName() );
					data = PlayerData.newPlayerData( player );
					data.setTimesLoggedIn( 1 );

					// Teleport first time users to spawn area
					{
						player.setBedSpawnLocation( plugin.getSpawnLocation().clone(), true );
						F.teleport( plugin, player, plugin.getSpawnLocation() );
					}

					// Make sure existing users don't have a nickname that matches this player's account name
					// and resolve if collision is found
					PlayerData nameCollision = plugin.getPlayerStore().getPlayerDataByPlayerNickname( player.getName() );
					if ( nameCollision != null ) {
						nameCollision.setNickname( nameCollision.getName() );
						PlayerData.save( nameCollision );
					}
				}

				// Returning users
				else {
					if ( data.getNickname() != null && !player.getName().equals( data.getNickname() ) ) {
						player.setDisplayName( data.getNickname() );
						player.setCustomName( data.getNickname() );
						player.setPlayerListName( data.getNickname() );
					}
					Log.line( "returning user: " + player.getName() + " aka " + player.getDisplayName() );
					data.setLastLogin( new Date() );
					data.setTimesLoggedIn( data.getTimesLoggedIn() + 1 );
				}

				// Save
				PlayerData.save( data );
			}

		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onPlayerQuitEvent( SafeSpawn plugin, PlayerQuitEvent event ) {
		try {
			Log.line( "onPlayerQuit: %s", event.getPlayer().getName() );
			Player player = event.getPlayer();
			plugin.getPlayerManager().removePlayerFromCache( player );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onEntityDamagedByEntity( SafeSpawn plugin, EntityDamageByEntityEvent event ) {
		try {
			// skip if another handler has already cancelled the event
			if ( event.isCancelled() ) {
				return;
			}

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
			Log.error( e );
		}
	}

	public static void onPlayerInteractEvent( SafeSpawn plugin, PlayerInteractEvent event ) {
		try {
			Player player = event.getPlayer();

			if ( event.getAction().equals( Action.LEFT_CLICK_BLOCK ) ) {
				Block block = event.getClickedBlock();

				// TeleporterSign
				if ( null != F.isOwnedWallSign( block.getLocation(), block.getType() ) ) {
					if ( event.getPlayer().hasPermission( Const.PERM_magic_sign ) ) {
						BlockData bd = BlockData.get( block );
						if ( Blocks.canAccess( bd, player ) ) {
							Sign state = F.blockToBlockSign( block );
							if ( state != null ) {
								boolean magical = Blocks.isMagical( BlockData.get( block ) );
								if ( magical ) {
									MagicSign sign = SignReader.readSign( (Sign) state );
									if ( sign.activateSign( state, event ) ) {
										player.sendMessage( MagicCommand.Teleport.getWord() );
									}
								}
							}
						}
					}
				}

				// Door
//				if ( F.isOwnedBlock( block.getLocation(), block.getType() ) ) { }
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onInventoryOpenEvent( SafeSpawn plugin, InventoryOpenEvent event ) {
		try {
			Log.line( "onInventoryOpenEvent" );
			InventoryHolder holder = event.getInventory().getHolder();

			if ( holder instanceof Chest || holder instanceof DoubleChest ) {
				Log.line( "onInventoryOpenEvent is Chest" );
				if ( event.getPlayer() instanceof Player ) {
					Player player = (Player) event.getPlayer();
					Log.line( "onInventoryOpenEvent is Block is Player" );

					Set<Block> signBlocks = Sets.newHashSet();

					if ( holder instanceof Chest ) { // check vertically only
						Chest c = (Chest) holder;
						signBlocks.addAll( F.findBlock( c.getLocation(), Material.WALL_SIGN, 0, 1, 0, true ) ); // vertical only
					} else if ( holder instanceof DoubleChest ) { // check vertically here and to the left 1 block vertically
						DoubleChest dc = (DoubleChest) holder;
						Log.line( "DC " + F.toString( dc.getLocation() ) );

						Set<Block> chests = F.findBlock( dc.getLocation(), Material.CHEST, 1, 0, 1, false ); // horizontal only

						for ( Block c : chests ) { // should only be 2
							Log.line( "FOUND: %s", F.toString( c ) );
							signBlocks.addAll( F.findBlock( c.getLocation(), Material.WALL_SIGN, 0, 1, 0, true ) ); // vertical only
						}
					}

					for ( Block s : signBlocks ) {
						Log.line( "FOUND SIGN: %s", F.toString( s ) );

						BlockData bd = BlockData.get( s );
						if ( bd != null && Blocks.isMagical( bd ) ) {
							if ( !Blocks.canAccess( bd, player ) ) {
								player.sendMessage( "This Chest is magically sealed, talk to the owner for access." );
								event.setCancelled( true );
								return;
							} else {
								player.sendMessage( "Access Granted" );
							}
						}
					}

					// Block block = (Block) holder;
					// Chest chest = Functions.blockToChest( block );
					// if ( chest != null ) {
					// Log.line( "onInventoryOpenEvent is Block is Player is Chest" );
					// BlockData bd = BlockData.get( block );
					// if ( bd != null && Blocks.isMagical( bd ) ) {
					// Log.line( "onInventoryOpenEvent is Block is Player is Chest is Magic" );
					// Player player = (Player) event.getPlayer();
					// if ( !Blocks.hasReadAccess( bd, player ) && !Blocks.hasWriteAccess( bd, player ) ) {
					// Log.line( "onInventoryOpenEvent is Block is Player is Chest is Magic has no access" );
					// player.sendMessage( "This Chest is magically sealed, talk to the owner for access." );
					// event.setCancelled( true );
					// return;
					// } else {
					// Log.line( "onInventoryOpenEvent is Block is Player is Chest is Magic has access" );
					// player.sendMessage( "Access Granted" );
					// }
					// }
					// }
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onAsyncPlayerChatEvent( SafeSpawn safeSpawn, AsyncPlayerChatEvent event ) {
		try {
			Log.line( "onPlayerChat: %s", event.getPlayer().getName() );
			PlayerData data = PlayerData.get( event.getPlayer() );
			if ( data != null ) {
				long firstTime = data.getFirstChatTime();
				long lastTime = data.getLastChatTime();
				long now = System.currentTimeMillis();
				int chats = data.getChatCount();
				int warns = data.getSpamWarnings();
				long diff = lastTime - firstTime;

				if ( firstTime == 0 ) { // init, no prev chats
					Log.line( "first time" );
					data.setFirstChatTime( now );
					data.setLastChatTime( now );
					data.setChatCount( 1 );
				} else if ( diff > Const.SPAM_PERIOD_MILLIS ) { // reset, no recent activity
					Log.line( "not first time but its been awhile: %s", diff );
					data.setFirstChatTime( now );
					data.setLastChatTime( now );
					data.setChatCount( 1 );
				} else { // recent activity
					Log.line( "not first time with %s recents: %s", chats, diff );
					if ( chats > Const.SPAM_TIMES_PER_PERIOD ) {
						Log.line( "not first time with %s recents: %s - exceed", chats, diff );
						if ( warns > 0 ) { // kick
							event.getPlayer().kickPlayer( "You were warned about spamming" );
							data.setSpamWarnings( 0 );
							data.setFirstChatTime( now );
							data.setLastChatTime( now );
							data.setChatCount( 1 );
						} else { // warn
							event.getPlayer().sendMessage( "You're about to get kicked for spamming... read /rules" );
							data.setSpamWarnings( 1 );
							data.setFirstChatTime( now );
							data.setLastChatTime( now );
							data.setChatCount( 1 );
						}
					} else { // recent activity but not spammy yet
						Log.line( "not spammy %s %s", chats, diff );
						data.setChatCount( chats + 1 );
						data.setLastChatTime( now );
					}
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

}
