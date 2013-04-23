package com.ath.bukkit.safespawn.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.PlayerData;

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
				if ( player.hasPermission( Const.PERM_magic_cast ) ) {
					PlayerData data = plugin.getPlayerStore().getPlayerData( player );
					data.setCasting( true );
					player.sendMessage( "Your next block or action will be magically enhanced, see /help-cast for help" );
					return true;
				}
			} else {
				sender.sendMessage( "Something evil has happened... we can't return you to spawn" );
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
		return false;
	}

}
