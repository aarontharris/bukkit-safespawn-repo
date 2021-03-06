package com.ath.bukkit.safespawn.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.ath.bukkit.safespawn.Log;
import com.avaje.ebean.EbeanServer;
import com.google.common.collect.Sets;

public class PlayerStore {

	private EbeanServer database;
	private Map<String, PlayerData> playerDataCache = new HashMap<String, PlayerData>();

	public PlayerStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		Log.error( e );
	}

	public Set<PlayerData> getPlayersInCache() {
		return Sets.newHashSet( playerDataCache.values() );
	}

	/** when the player logs out, remove from cache */
	public void removeFromCache( String playerName ) {
		try {
			if ( playerName != null ) {
				playerDataCache.remove( playerName );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public PlayerData getPlayerDataFromCache( Player player ) {
		if ( player != null ) {
			return playerDataCache.get( player.getName() );
		}
		return null;
	}

	/**
	 * @param player
	 * @return null would suggest a new user, or database was deleted
	 */
	public PlayerData getPlayerData( Player player ) {
		try {
			if ( player != null ) {
				PlayerData data = playerDataCache.get( player.getName() );
				if ( data == null ) {
					data = getPlayerDataByPlayer( player );
					if ( data != null ) {
						playerDataCache.put( data.getName(), data );
					}
				}
				return data;
			}
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	void savePlayerData( PlayerData playerData ) {
		try {
			if ( playerData != null ) {
				playerDataCache.put( playerData.getName(), playerData );
				savePlayerDataToDb( playerData );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public boolean isNicknameAvailable( Player requestor, String nickname ) {
		try {
			// allow player to set nickname back to real name
			if ( requestor != null && requestor.getName().equals( nickname ) ) {
				return true;
			}

			PlayerData player = getPlayerDataByPlayerName( nickname );
			if ( player == null ) { // don't allow players to use other player's account names as nicknames
				PlayerData nick = getPlayerDataByPlayerNickname( nickname );
				if ( nick == null ) {
					return true;
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	private PlayerData getPlayerDataByPlayer( Player player ) {
		if ( player != null ) {
			return getPlayerDataByPlayerName( player.getName() );
		}
		return null;
	}

	public PlayerData getPlayerDataByPlayerNickname( String nickname ) {
		try {
			PlayerData out = database.find( PlayerData.class ).where().ieq( PlayerData.NICKNAME, nickname ).findUnique();
			return out;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	public PlayerData getPlayerDataByPlayerName( String name ) {
		try {
			PlayerData out = database.find( PlayerData.class ).where().ieq( PlayerData.NAME, name ).findUnique();
			return out;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private void savePlayerDataToDb( PlayerData playerData ) {
		try {
			if ( playerData != null && playerData.getName() != null ) {
				PlayerData data = database.find( PlayerData.class ).where().ieq( PlayerData.NAME, playerData.getName() ).findUnique();
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

	public boolean isPlayerLevitateEnabled( PlayerData pd ) {
		return pd.getBoolean( "levitate", false );
	}

	public void setPlayerLevitateEnabled( PlayerData pd, boolean enabled ) {
		pd.putBoolean( "levitate", false );
	}
}
