package com.ath.bukkit.safespawn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.ath.bukkit.safespawn.Zone.ZoneExclude;
import com.ath.bukkit.safespawn.cmd.LinesReaderCmd;
import com.ath.bukkit.safespawn.cmd.SpawnCmd;

public class SafeSpawnPlugin extends JavaPlugin {

	private Logger logger;
	private Set<Zone> zones;
	private Map<String, Player> playerCache;
	private Location spawnLocation;

	@Override
	public void onLoad() {
		super.onLoad();
		logger = getLogger();
	}

	@Override
	public void onEnable() {
		try {
			initializeConfig();
			initializeEvents();
			initializeCommands();
			initializePlayerCache();
		} catch ( Exception e ) {
			logger.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	private void initializeConfig() {
		this.saveDefaultConfig(); // this does not overwrite if config.yml already exists, bad naming...

		// List<World> worlds = getServer().getWorlds();
		// if ( worlds != null ) {
		// for ( World w : worlds ) {
		// logger.info( String.format( "World %s=%s", w.getName(), w.getUID() ) );
		// }
		// }

		zones = Zone.fromConfig( this );

		try {
			ConfigurationSection spawnpoint = getConfig().getConfigurationSection( Const.CFG_spawnpoint );
			World spawnWorld = getServer().getWorld( spawnpoint.getString( Const.CFG_spawnpoint_world ) );
			int x = spawnpoint.getInt( Const.CFG_spawnpoint_x );
			int y = spawnpoint.getInt( Const.CFG_spawnpoint_y );
			int z = spawnpoint.getInt( Const.CFG_spawnpoint_z );
			spawnLocation = new Location( spawnWorld, x, y, z );
		} catch ( Exception e ) {
			logger.log( Level.SEVERE, "Something was wrong with your spawnpoint config, maybe a bad/missing world name? or xyz?" );
			logger.log( Level.SEVERE, e.getMessage(), e );
		}
	}

	private void initializeEvents() {
		getServer().getPluginManager().registerEvents( new Listener() {
			@EventHandler
			public void playerJoin( PlayerJoinEvent event ) {
				Player player = event.getPlayer();
				playerCache.put( player.getName(), player );
				player.sendMessage( getConfig().getString( Const.MSG_welcome_message ) );
			}

			@EventHandler
			public void playerLeave( PlayerQuitEvent event ) {
				// TODO: make sure when a player is banned or kicked that it still calls the PlayerQuitEvent
				Player player = event.getPlayer();
				playerCache.remove( player );
			}

			@EventHandler
			public void blockBreakEvent( BlockBreakEvent event ) {
				for ( Zone zone : zones ) {
					Block block = event.getBlock();
					Player player = event.getPlayer();
					if ( zone.isTouching( block.getLocation() ) && zone.hasExclude( ZoneExclude.BLOCK_BREAK ) ) {
						if ( !player.isPermissionSet( Const.PERM_blockbreak ) && !player.isPermissionSet( Const.PERM_blockbreak + "." + zone.getName() ) ) {
							event.setCancelled( true );
							Location l = zone.getLocation();
							player.sendMessage( "Zone: " + zone.getName() + ", Radius: " + zone.getRadius() + " @ " + l.getX() + ", " + l.getY() + ", " + l.getZ() );
							player.sendMessage( "This is a no block breaking zone" );
						}
					}
				}
			}

			@EventHandler
			public void blockPlaceEvent( BlockPlaceEvent event ) {
				// TODO: ATH-P3 -- make me more efficient
				// P3 because there are unlikely to be many zones as long as they are only created within the config.yml
				// - - if I allow others to create zones within the game, then we really need to bump up the priority.
				// I should be able to hash down to a narrow list of zones based on a world or even a chunk?
				// maybe only get zones that care about the event:
				// - - EX: onBlockPlaceEvent, only get zones that exclude the BlockPlaceEvent
				for ( Zone zone : zones ) {
					Block block = event.getBlock();
					Player player = event.getPlayer();
					if ( zone.isTouching( block.getLocation() ) && zone.hasExclude( ZoneExclude.BLOCK_PLACE ) ) {
						if ( !player.isPermissionSet( Const.PERM_blockplace ) && !player.isPermissionSet( Const.PERM_blockplace + "." + zone.getName() ) ) {
							event.setCancelled( true );
							Location l = zone.getLocation();
							player.sendMessage( "Zone: " + zone.getName() + ", Radius: " + zone.getRadius() + " @ " + l.getX() + ", " + l.getY() + ", " + l.getZ() );
							player.sendMessage( "This is a no block placement protected zone" );
						}
					}
				}
			}
		}, this );
	}

	private void initializeCommands() {
		getCommand( Const.CMD_rules ).setExecutor( new LinesReaderCmd( this, Const.MSG_rules ) );
		getCommand( Const.CMD_gamedesc ).setExecutor( new LinesReaderCmd( this, Const.MSG_gamedesc ) );
		getCommand( Const.CMD_spawn ).setExecutor( new SpawnCmd( this ) );
	}

	private void initializePlayerCache() {
		playerCache = new HashMap<String, Player>();
		for ( World world : getServer().getWorlds() ) {
			for ( Player player : world.getPlayers() ) {
				playerCache.put( player.getName(), player );
			}
		}
	}

	public Player getActivePlayerByName( String name ) {
		if ( playerCache != null ) {
			return playerCache.get( name );
		}
		return null;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}
}
