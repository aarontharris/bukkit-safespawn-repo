package com.ath.bukkit.safespawn.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;

@Entity
@Table( name = "PlayerData" )
public class PlayerData implements Persisted {
	public static final String NAME = "name";
	public static final String NICKNAME = "nickname";

	public static final String[] SCHEMA = {
			"create table PlayerData" +
					"(" +
					"name TEXT," +
					"nickname TEXT," +
					"firstLogin timestamp," +
					"lastLogin timestamp," +
					"timesLoggedIn INTEGER," +
					"flags BLOB," +
					"id INTEGER primary key autoincrement" +
					");",
			"CREATE INDEX PlayerData_name_INDEX ON PlayerData (name);",
			"CREATE INDEX PlayerData_nickname_INDEX ON PlayerData (nickname);"
	}; // alter table PlayerData add column nickname TEXT;

	/*
	 * BEGIN TRANSACTION;
	 * CREATE TEMPORARY TABLE backup as select * from PlayerData;
	 * DROP TABLE PlayerData;
	 * // paste create schema
	 * INSERT INTO PlayerData SELECT * FROM backup;
	 * DROP TABLE backup;
	 * COMMIT;
	 */

	@Override
	public String[] getSchema() {
		return SCHEMA;
	}

	@Id
	private int id;

	@Column( name = "name", unique = true )
	private String name;

	@Column( name = "nickname", unique = true )
	private String nickname;

	@Column( name = "firstLogin" )
	private Date firstLogin;

	@Column( name = "lastLogin" )
	private Date lastLogin;

	@Column( name = "timesLoggedIn" )
	private int timesLoggedIn = 0;

	@Column( name = "flags" )
	private String flags;

	private transient JSONObject flagsJSON;

	private transient boolean modified = false;
	private transient int chatCount = 0;
	private transient long firstChatTime = 0;
	private transient long lastChatTime = 0;
	private transient int spamWarnings = 0;

	public PlayerData() {
	}

	public static PlayerData newPlayerData( Player player ) {
		PlayerData out = new PlayerData();
		out.setName( player.getName() );
		out.setFirstLogin( new Date() );
		out.setLastLogin( out.getFirstLogin() );
		return out;
	}

	public static void save( PlayerData data ) {
		try {
			SafeSpawn.instance().getPlayerStore().savePlayerData( data );
			data.setModified( false );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static PlayerData get( Player player ) {
		return SafeSpawn.instance().getPlayerStore().getPlayerData( player );
	}

	@SuppressWarnings( "unchecked" )
	public void putString( String key, String value ) {
		try {
			getFlagsJSON().put( key, value );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public String getString( String key, String defaultValue ) {
		try {
			String out = (String) getFlagsJSON().get( key );
			if ( out != null ) {
				return out;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return defaultValue;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Date getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin( Date firstLogin ) {
		this.firstLogin = firstLogin;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin( Date lastLogin ) {
		this.lastLogin = lastLogin;
	}

	public int getTimesLoggedIn() {
		return timesLoggedIn;
	}

	public void setTimesLoggedIn( int timesLoggedIn ) {
		this.timesLoggedIn = timesLoggedIn;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	/** @deprecated - used by ORM */
	public String getFlags() {
		this.flags = getFlagsJSON().toString();
		return this.flags;
	}

	/** @deprecated - used by ORM */
	public void setFlags( String flags ) {
		try {
			if ( flags == null || flags.isEmpty() ) {
				flags = "{}";
			}
			JSONParser parser = new JSONParser();
			this.flags = flags;
			this.flagsJSON = (JSONObject) parser.parse( flags );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	private JSONObject getFlagsJSON() {
		if ( flagsJSON == null ) {
			flagsJSON = new JSONObject();
		}
		return flagsJSON;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname( String nickname ) {
		this.nickname = nickname;
	}

	public int getChatCount() {
		return chatCount;
	}

	public void setChatCount( int chatCount ) {
		this.chatCount = chatCount;
	}

	/** millis */
	public long getLastChatTime() {
		return lastChatTime;
	}

	/** millis */
	public void setLastChatTime( long lastChatTime ) {
		this.lastChatTime = lastChatTime;
	}

	public int getSpamWarnings() {
		return spamWarnings;
	}

	public void setSpamWarnings( int spamWarnings ) {
		this.spamWarnings = spamWarnings;
	}

	public long getFirstChatTime() {
		return firstChatTime;
	}

	public void setFirstChatTime( long firstChatTime ) {
		this.firstChatTime = firstChatTime;
	}

	public boolean isModified() {
		return modified;
	}

	private void setModified( boolean modified ) {
		this.modified = modified;
	}

}
