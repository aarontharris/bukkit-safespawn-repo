package com.ath.bukkit.safespawn.cmd;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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

						// Charge
						if ( args.length == 1 && args[0].equalsIgnoreCase( MagicCast.Charge.getWord() ) ) {
							return doCharge( player, cmd, label, args );
						}

						// Lock
						if ( args.length == 1 && args[0].equalsIgnoreCase( MagicCast.Lock.getWord() ) ) {
							return doLock( player, cmd, label, args );
						}

					}
				}
			} else {
				sender.sendMessage( "Something evil has happened... Your cast failed..." );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		Log.line( "%s failed cast %s", sender.getName(), Functions.joinSpace( args ) );
		return false;
	}

	protected boolean doCharge( Player player, Command cmd, String label, String[] args ) {
		PlayerData.setCasting( player, true );
		player.sendMessage( "Your next placed block or sign will be magically enhanced, see /help-cast for help" );
		Log.line( "%s successfully cast %s", player.getName(), Functions.joinSpace( args ) );
		return true;
	}

	protected boolean doLock( Player player, Command cmd, String label, String[] args ) {
		// get blocks in line of sight
		List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );

		// find the chest block
		Block block = null;
		for ( Block b : blocks ) {
			if ( b.getType().equals( Material.CHEST ) ) {
				block = b;
				break;
			}
		}
		Chest chest = Functions.blockToChest( block );
		BlockData bd = BlockData.get( block );

		// distance or no chest fail
		if ( chest == null ) {
			player.sendMessage( "You're too far from a chest" );
			Log.line( "%s failed cast %s -- too far from a chest", player.getName(), Functions.joinSpace( args ) );
			return false;
		}

		// initial lock, self access only
		if ( Blocks.hasWriteAccess( bd, player ) ) {
			if ( args.length == 1 ) {
				Blocks.grantWriteAccess( block, player );
			}
		}

		return true;
	}

}
