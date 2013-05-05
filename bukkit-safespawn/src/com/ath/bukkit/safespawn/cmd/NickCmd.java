package com.ath.bukkit.safespawn.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.PlayerData;

public class NickCmd implements CommandExecutor {

	private SafeSpawn plugin;

	public NickCmd( SafeSpawn plugin ) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		try {
			Player player = plugin.getPlayerManager().getActivePlayerByName( sender.getName() );
			if ( player != null ) {
				String nickname = player.getName();

				if ( args.length == 0 ) {
					player.sendMessage( "No nickname given, resetting to " + nickname );
				} else if ( args.length == 1 ) {
					nickname = args[0];
				} else {
					player.sendMessage( "Invalid usage of /nick.  Try /nick newNickName" );
					return false;
				}

				if ( plugin.getPlayerStore().isNicknameAvailable( player, nickname ) ) {
					PlayerData data = PlayerData.get( player );
					data.setNickname( nickname );
					PlayerData.save( data );
					player.setDisplayName( nickname );
					player.setCustomName( nickname );
					player.setPlayerListName( nickname );
					player.sendMessage( "Your nickname has been changed" );
					Log.line( "onCommand(nick) %s aka %s -- success", sender.getName(), nickname );
				} else {
					player.sendMessage( nickname + " is unavailable" );
					return false;
				}

			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}
}
