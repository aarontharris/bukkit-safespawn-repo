package com.ath.bukkit.safespawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class CfgHelper {

	public static Location readLocationFromConfig( ConfigurationSection section ) {
		try {
			World world = SafeSpawn.instance().getServer().getWorld( section.getString( "world" ) );
			int x = section.getInt( "x" );
			int y = section.getInt( "y" );
			int z = section.getInt( "z" );
			Location out = new Location( world, x, y, z );
			return out;
		} catch ( Exception e ) {
			SafeSpawn.logLine( "Something was wrong with your config location, maybe a bad/missing world name? or xyz?" );
			SafeSpawn.logError( e );
		}
		return null;
	}

}
