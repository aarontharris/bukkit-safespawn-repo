package com.ath.bukkit.safespawn.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.SafeSpawn;

public class SpawnCmd implements CommandExecutor {

	private SafeSpawn plugin;

	public SpawnCmd( SafeSpawn plugin ) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		try {
			Player player = plugin.getPlayerManager().getActivePlayerByName( sender.getName() );
			if ( player != null ) {
				Functions.teleport( plugin, player, plugin.getSpawnLocation() );
				return true;
			} else {
				sender.sendMessage( "Something evil has happened... we can't return you to spawn" );
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
		return false;
	}

}
