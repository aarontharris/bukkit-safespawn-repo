package com.ath.bukkit.safespawn;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerManager {

	private SafeSpawn plugin;

	private Map<Integer, Player> entityIdToPlayerMap;
	private Map<String, Player> nameToPlayerMap;

	public PlayerManager( SafeSpawn plugin ) {
		this.plugin = plugin;
		entityIdToPlayerMap = new HashMap<Integer, Player>();
		nameToPlayerMap = new HashMap<String, Player>();
	}

	public void initialize() {
		for ( World world : plugin.getServer().getWorlds() ) {
			for ( Player player : world.getPlayers() ) {
				cachePlayer( player );
			}
		}
	}

	public Player getActivePlayerByName( String name ) {
		return nameToPlayerMap.get( name );
	}

	public Player getActivePlayerByEntityId( int entityId ) {
		return entityIdToPlayerMap.get( entityId );
	}

	public void cachePlayer( Player player ) {
		if ( player != null ) {
			entityIdToPlayerMap.put( player.getEntityId(), player );
			nameToPlayerMap.put( player.getName(), player );
		}
	}

	public void removePlayerFromCache( Player player ) {
		if ( player != null ) {
			entityIdToPlayerMap.remove( player.getEntityId() );
			nameToPlayerMap.remove( player.getName() );
		}
	}

}
