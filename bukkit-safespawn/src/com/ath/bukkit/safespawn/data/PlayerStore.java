package com.ath.bukkit.safespawn.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.avaje.ebean.EbeanServer;

public class PlayerStore {

	private EbeanServer database;
	private Map<Integer, PlayerData> playerDataCache = new HashMap<Integer, PlayerData>();

	public PlayerStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		SafeSpawn.logError( e );
	}

	/** when the player logs out, remove from cache */
	public void removeFromCache( Player player ) {
		try {
			if ( player != null ) {
				playerDataCache.remove( player.getEntityId() );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	/**
	 * @param player
	 * @return null would suggest a new user, or database was deleted
	 */
	public PlayerData getPlayerData( Player player ) {
		try {
			if ( player != null ) {
				PlayerData data = playerDataCache.get( player.getEntityId() );
				if ( data == null ) {
					data = getPlayerDataByPlayer( player );
					if ( data != null ) {
						playerDataCache.put( data.getId(), data );
					}
				}
				return data;
			}
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	public void savePlayerData( PlayerData playerData ) {
		try {
			if ( playerData != null ) {
				playerDataCache.put( playerData.getId(), playerData );
				savePlayerDataToDb( playerData );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	private PlayerData getPlayerDataByPlayer( Player player ) {
		if ( player != null ) {
			return getPlayerDataByPlayerId( player.getEntityId() );
		}
		return null;
	}

	private PlayerData getPlayerDataByPlayerId( int playerId ) {
		try {
			PlayerData out = database.find( PlayerData.class ).where().idEq( playerId ).findUnique();
			return out;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private void savePlayerDataToDb( PlayerData playerData ) {
		try {
			if ( playerData != null && playerData.getId() >= 0 ) {
				PlayerData data = database.find( PlayerData.class ).where().idEq( playerData.getId() ).findUnique();
				if ( data != null ) {
					database.update( playerData );
				} else {
					database.save( playerData );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}
}
