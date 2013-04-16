package com.ath.bukkit.safespawn;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SafeSpawnPlugin extends JavaPlugin {

	private Logger logger;

	@Override
	public void onLoad() {
		super.onLoad();
		logger = getLogger();
	}

	@Override
	public void onEnable() {
		try {
			this.saveDefaultConfig(); // this does not overwrite if config.yml already exists, bad naming...

			getServer().getPluginManager().registerEvents( new Listener() {
				@EventHandler
				public void playerJoin( PlayerJoinEvent event ) {
					event.getPlayer().sendMessage( getConfig().getString( "message" ) );
				}
			}, this );

			getCommand( "rules" ).setExecutor( new CommandExecutor() {
				@Override
				public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
					List<String> rules = getConfig().getStringList( "rules" );
					for ( String rule : rules ) {
						sender.sendMessage( rule );
					}
					return true;
				}
			} );

		} catch ( Exception e ) {
			logger.log( Level.SEVERE, e.getMessage(), e );
		}
	}
}
