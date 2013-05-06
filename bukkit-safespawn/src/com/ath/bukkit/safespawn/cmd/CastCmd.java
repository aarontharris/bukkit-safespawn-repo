package com.ath.bukkit.safespawn.cmd;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.BlockData;
import com.ath.bukkit.safespawn.data.Blocks;
import com.ath.bukkit.safespawn.data.PlayerData;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicCast;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CastCmd implements CommandExecutor {

	private SafeSpawn plugin;

	public CastCmd( SafeSpawn plugin ) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		try {
			Player player = plugin.getPlayerManager().getActivePlayerByName( sender.getName() );
			if ( player != null ) {
				Log.line( "CastCmd: %s, %s, %s", player.getName(), player.getCustomName(), player.getDisplayName() );
				if ( player.hasPermission( Const.PERM_magic_cast ) ) {
					if ( args.length > 0 ) {
						MagicCast castWord = null;
						try {
							castWord = MagicCast.fromWord( args[0] );
						} catch ( Exception e ) {
							Log.error( e );
						}

						if ( castWord != null ) {

							switch ( castWord ) {
							case Charge:
								return doCharge( player, cmd, label, args );
							case Lock:
								return doLock( player, cmd, label, args );
							case Unlock:
								return doUnlock( player, cmd, label, args );
							case Grant:
								return doGrant( player, cmd, label, args );
							case Revoke:
								return doRevoke( player, cmd, label, args );
							case Access:
								return doAccess( player, cmd, label, args );
								// case Levitate: return doCharge( player, cmd, label, args );
							default:
								break;
							}
						}
					}

					player.sendMessage( "Nothing happens... Perhaps you mispronounced the spell?" );
					return false;
				}
			} else {
				sender.sendMessage( "Something evil has happened... Your cast failed..." );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// /cast charge
	protected boolean doCharge( Player player, Command cmd, String label, String[] args ) {
		PlayerData.setCasting( player, true );
		player.sendMessage( "Your next placed block or sign will be magically enhanced, see /help-cast for help" );
		Log.line( "%s successfully cast %s", player.getName(), Functions.joinSpace( args ) );
		return true;
	}

	// /cast lock
	protected boolean doLock( Player player, Command cmd, String label, String[] args ) {
		Block block = Functions.getTargetBlock( player, 5, Material.WALL_SIGN );

		// distance or no chest fail
		if ( block == null ) {
			player.sendMessage( "You're too far from a Wall Sign" );
			return false;
		}

		BlockData bd = BlockData.get( block );

		// initial lock, self access only
		if ( Blocks.canWrite( bd, player ) ) {
			Blocks.grantWriteAccess( block, player );
			Blocks.setMagical( block, true );
			player.sendMessage( MagicCast.Lock.getMagicWord() + " -- lock successful" );
		} else {
			player.sendMessage( "You don't have permission." );
		}

		return true;
	}

	// /cast unlock
	protected boolean doUnlock( Player player, Command cmd, String label, String[] args ) {
		Block block = Functions.getTargetBlock( player, 5, Material.WALL_SIGN );
		// distance or no chest fail
		if ( block == null ) {
			player.sendMessage( "You're too far from a Wall Sign" );
			return false;
		}

		BlockData bd = BlockData.get( block );

		// initial lock, self access only
		if ( Blocks.canWrite( bd, player ) ) {
			Blocks.clearReadWriteAccess( block );
			player.sendMessage( MagicCast.Unlock.getMagicWord() + " -- unlock successful" );
		} else {
			player.sendMessage( "You don't have permission." );
		}

		return true;
	}

	// /cast grant playername1,playername2,playername3
	protected boolean doGrant( Player player, Command cmd, String label, String[] args ) {
		List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );
		Block block = blocks.get( 1 );
		BlockData bd = BlockData.get( block );

		// verify usage
		if ( args.length < 2 ) {
			player.sendMessage( "Nothing happens... Try /cast grant playername" );
			return false;
		}

		// translate playernames
		Set<String> newAccess = null;
		if ( args[1].contains( "," ) ) {
			newAccess = Sets.newHashSet( args[1].split( "," ) );
		} else {
			newAccess = Sets.newHashSet( args[1] );
		}

		// check permissions
		if ( newAccess != null ) {
			if ( Blocks.isMagical( bd ) ) {
				Set<String> access = Blocks.getWriteAccess( bd );
				if ( Blocks.hasWriteAccess( bd, access, player ) ) { // FIXME: switch to Blocks.isOwner( bd, player )
					access.addAll( newAccess );
					Blocks.setWriteAccess( block, access );
					player.sendMessage( "Granted" );
				} else {
					player.sendMessage( "You don't have permission." );
				}
			} else {
				player.sendMessage( "That is not a magical block" );
			}
		}

		return true;
	}

	// /cast revoke playername1,playername2,playername3
	protected boolean doRevoke( Player player, Command cmd, String label, String[] args ) {
		List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );
		Block block = blocks.get( 1 );
		BlockData bd = BlockData.get( block );

		// verify usage
		if ( args.length < 2 ) {
			player.sendMessage( "Nothing happens... Try /cast grant playername" );
			return false;
		}

		// translate playernames
		List<String> access = null;
		if ( args[1].contains( "," ) ) {
			access = Lists.newArrayList( args[1].split( "," ) );
		} else {
			access = Lists.newArrayList( args[1] );
		}

		// check permissions
		if ( access != null ) {
			if ( Blocks.isMagical( bd ) ) {
				if ( Blocks.canWrite( bd, player ) ) {
					Set<String> writes = Blocks.getWriteAccess( bd );
					writes.removeAll( access );
					Blocks.setWriteAccess( block, writes );
				} else {
					player.sendMessage( "You don't have permission." );
				}
			} else {
				player.sendMessage( "That is not a magical block" );
			}
		}

		return true;
	}

	// /cast access
	private boolean doAccess( Player player, Command cmd, String label, String[] args ) {
		Log.line( player.getName() + ".doAccess" );

		List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );
		Functions.debugBlocks( blocks );
		if ( blocks == null || blocks.isEmpty() ) {
			player.sendMessage( "What is it you're trying to access?" );
			return false;
		}

		Block block = blocks.get( 1 );
		Functions.debugBlock( block );

		BlockData bd = BlockData.get( block );
		if ( bd == null ) {
			player.sendMessage( "You've tried to access a non-magical " + block.getType() );
			return false;
		}

		Log.line( "BD: " + bd.getHash() );

		if ( Blocks.isMagical( bd ) ) {
			Log.line( "is magical" );
			Set<String> access = Blocks.getWriteAccess( bd );
			if ( Blocks.hasWriteAccess( bd, access, player ) ) {
				Log.line( "has write" );
				if ( access.isEmpty() ) {
					player.sendMessage( "Write: none" );
				} else {
					player.sendMessage( "Write: " + Functions.joinSpace( access ) );
				}
			} else {
				player.sendMessage( "You don't have permission." );
			}
		} else {
			player.sendMessage( "You've tried to access a non-magical " + block.getType() );
		}

		return true;
	}
}
