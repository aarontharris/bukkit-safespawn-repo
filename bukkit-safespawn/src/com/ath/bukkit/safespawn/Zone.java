package com.ath.bukkit.safespawn;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Zone {
	private static final String ZONEKEY_description = "description";
	private static final String ZONEKEY_radius = "radius";
	private static final String ZONEKEY_world = "world";
	private static final String ZONEKEY_exclude = "exclude";
	private static final String ZONEKEY_x = "x";
	private static final String ZONEKEY_y = "y";
	private static final String ZONEKEY_z = "z";



	public static enum ZoneExclude {
		BLOCK_BREAK, BLOCK_PLACE
	}

	public static Set<Zone> fromConfig( JavaPlugin plugin ) {
		Set<Zone> out = new HashSet<Zone>();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection( "zones" );
		Map<String, Object> values = section.getValues( false );
		for ( String key : values.keySet() ) {
			ConfigurationSection zoneCfg = section.getConfigurationSection( key );
			if ( zoneCfg == null ) {
				continue;
			}

			try {
				Zone zone = new Zone();
				out.add( zone );

				// name
				zone.setName( zoneCfg.getName() );

				// description
				zone.setDescription( zoneCfg.getString( ZONEKEY_description ) );

				// radius
				zone.setRadius( zoneCfg.getInt( ZONEKEY_radius ) );

				// world
				{
					String worldName = zoneCfg.getString( ZONEKEY_world );
					World world = plugin.getServer().getWorld( worldName );
					zone.setWorld( world );
				}

				// location
				{
					int x = zoneCfg.getInt( ZONEKEY_x );
					int y = zoneCfg.getInt( ZONEKEY_y );
					int z = zoneCfg.getInt( ZONEKEY_z );
					Location location = new Location( zone.getWorld(), x, y, z );
					zone.setLocation( location );
				}

				// excludes
				{
					List<String> excludes = zoneCfg.getStringList( ZONEKEY_exclude );
					if ( excludes != null && !excludes.isEmpty() ) {
						for ( String exStr : excludes ) {
							try {
								zone.addExclude( ZoneExclude.valueOf( exStr ) );
							} catch ( Exception e ) {
								plugin.getLogger().log( Level.SEVERE, e.getMessage(), e );
							}
						}
					}
				}

				plugin.getLogger().info( "Imported Zone: " + zone );
			} catch ( Exception e ) {
				plugin.getLogger().log( Level.SEVERE, "Something bad happened, skipping zone " + zoneCfg.getName() );
				plugin.getLogger().log( Level.SEVERE, e.getMessage(), e );
			}
		}

		return out;
	}

	private String name;
	private String description;
	private Set<ZoneExclude> exclude;
	private int radius;
	private World world;
	private Location location;

	public Zone() {
	}

	public boolean isTouching( Location l ) {
		if ( l != null ) {
			if ( this.getWorld() == l.getWorld() ) {
				double dist = getLocation().distance( l );
				if ( dist <= getRadius() ) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "name: " ).append( "\"" ).append( getName() ).append( "\"" ).append( ", " );
		sb.append( "description: " ).append( "\"" ).append( getDescription() ).append( "\"" ).append( ", " );
		sb.append( "radius: " ).append( "\"" ).append( getRadius() ).append( "\"" ).append( ", " );
		if ( getWorld() != null ) {
			sb.append( "world: " ).append( "\"" ).append( getWorld().getName() ).append( "\"" ).append( ", " );
		}
		if ( getLocation() != null ) {
			Location l = getLocation();
			sb.append( "location: " ).append( "\"" ).append( l.getX() + ", " + l.getY() + ", " + l.getZ() ).append( "\"" ).append( ", " );
		}
		if ( exclude != null ) {
			sb.append( "exclude: " ).append( "\"" ).append( exclude ).append( "\"" ).append( ", " );
		}
		return sb.toString();
	}

	private Set<ZoneExclude> getExclude() {
		if ( exclude == null ) {
			exclude = new HashSet<ZoneExclude>();
		}
		return exclude;
	}

	public boolean hasExclude( ZoneExclude ex ) {
		return getExclude().contains( ex );
	}

	public void addExclude( ZoneExclude ex ) {
		getExclude().add( ex );
	}

	public void remExclude( ZoneExclude ex ) {
		getExclude().remove( ex );
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius( int radius ) {
		this.radius = radius;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld( World world ) {
		this.world = world;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation( Location location ) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

}
