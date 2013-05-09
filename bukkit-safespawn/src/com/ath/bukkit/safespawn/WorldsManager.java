package com.ath.bukkit.safespawn;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class WorldsManager {
	private SafeSpawn plugin;
	private Map<String, String> magicToName;

	public WorldsManager( SafeSpawn plugin ) {
		this.plugin = plugin;
		magicToName = new HashMap<String, String>();
	}

	public void initialize() {
		try {
			ConfigurationSection worldsCfg = plugin.getConfig().getConfigurationSection( Const.CFG_worlds );
			ConfigurationSection cfg;

			cfg = worldsCfg.getConfigurationSection( Const.CFG_worlds_world );
			add( cfg.getString( "magic" ), cfg.getString( "name" ) );

			cfg = worldsCfg.getConfigurationSection( Const.CFG_worlds_world_nether );
			add( cfg.getString( "magic" ), cfg.getString( "name" ) );

			cfg = worldsCfg.getConfigurationSection( Const.CFG_worlds_world_the_end );
			add( cfg.getString( "magic" ), cfg.getString( "name" ) );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	private void add( String magic, String name ) {
		magicToName.put( magic, name );
	}

	public String findByMagic( String magic ) {
		return magicToName.get( magic );
	}
}
