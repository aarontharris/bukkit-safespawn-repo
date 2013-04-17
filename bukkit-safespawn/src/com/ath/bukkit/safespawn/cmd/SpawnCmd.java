package com.ath.bukkit.safespawn.cmd;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.SafeSpawnPlugin;

public class SpawnCmd implements CommandExecutor {

	private SafeSpawnPlugin plugin;

	public SpawnCmd( SafeSpawnPlugin plugin ) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		if ( sender.isPermissionSet( Const.CFG_spawn ) ) {
			Player player = plugin.getActivePlayerByName( sender.getName() );
			if ( player != null ) {
				Location spawn = plugin.getSpawnLocation().clone();
				spawn.setPitch( player.getLocation().getPitch() );
				spawn.setYaw( player.getLocation().getYaw() );
				player.teleport( spawn );
				return true;
			} else {
				sender.sendMessage( "Something evil has happened... we can't return you to spawn" );
			}
		}
		return false;
	}

}
