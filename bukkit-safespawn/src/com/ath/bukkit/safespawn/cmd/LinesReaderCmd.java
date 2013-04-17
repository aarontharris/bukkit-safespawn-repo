package com.ath.bukkit.safespawn.cmd;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.SafeSpawnPlugin;

/**
 * Simply prints a list of messages for the given command<br>
 * 
 * @author aharris
 */
public class LinesReaderCmd implements CommandExecutor {
	private SafeSpawnPlugin plugin;
	private String cfgKey;

	/**
	 * @param cfgKey - must be a valid list of strings defined in config.yml
	 */
	public LinesReaderCmd( SafeSpawnPlugin plugin, String cfgKey ) {
		this.plugin = plugin;
		this.cfgKey = cfgKey;
	}

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		try {
			List<String> lines = plugin.getConfig().getStringList( cfgKey );
			if ( lines != null && !lines.isEmpty() ) {
				for ( String line : lines ) {
					sender.sendMessage( line );
				}
			} else {
				sender.sendMessage( Const.MISSING_MSG );
			}
		} catch ( Exception e ) {
			plugin.getLogger().log( Level.SEVERE, e.getMessage(), e );
		}
		return true;
	}
}
