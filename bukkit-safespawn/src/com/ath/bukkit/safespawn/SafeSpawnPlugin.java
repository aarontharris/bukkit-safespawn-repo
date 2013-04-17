package com.ath.bukkit.safespawn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.ath.bukkit.safespawn.cmd.LinesReaderCmd;
import com.ath.bukkit.safespawn.cmd.SpawnCmd;
import com.ath.bukkit.safespawn.data.PlayerDao;
import com.ath.bukkit.safespawn.event.BlockEventHandler;
import com.ath.bukkit.safespawn.event.PlayerJoinLeaveHandler;

public class SafeSpawnPlugin extends JavaPlugin {

	private Logger logger;
	private Set<Zone> zones;
	private Map<String, Player> playerCache;
	private Location spawnLocation;
	private PlayerDao dao;

	@Override
	public void onLoad() {
		super.onLoad();
		logger = getLogger();
		dao = new PlayerDao( this );
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

		// FIXME: if this fails, the plugin is useless
		// the plugin should shut its services down
		// maybe just disable the server?
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
				PlayerJoinLeaveHandler.onPlayerJoin( SafeSpawnPlugin.this, event );
			}

			@EventHandler
			public void playerLeave( PlayerQuitEvent event ) {
				PlayerJoinLeaveHandler.onPlayerLeave( SafeSpawnPlugin.this, event );
			}

			@EventHandler
			public void blockBreakEvent( BlockBreakEvent event ) {
				BlockEventHandler.onBlockBreakEvent( SafeSpawnPlugin.this, event );
			}

			@EventHandler
			public void blockPlaceEvent( BlockPlaceEvent event ) {
				BlockEventHandler.onBlockPlaceEvent( SafeSpawnPlugin.this, event );
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
		return playerCache.get( name );
	}

	public void cachePlayer( Player player ) {
		if ( player != null ) {
			playerCache.put( player.getName(), player );
		}
	}

	public void removePlayerFromCache( Player player ) {
		if ( player != null ) {
			playerCache.remove( player.getName() );
		}
	}

	public PlayerDao getPlayerDao() {
		return dao;
	}

	public Set<Zone> getZones() {
		return zones;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void logError( Exception e ) {
		logger.log( Level.SEVERE, e.getMessage() );
		for ( StackTraceElement el : e.getStackTrace() ) {
			logger.log( Level.SEVERE, el.getFileName() + ":" + el.getLineNumber() );
		}
	}
}
