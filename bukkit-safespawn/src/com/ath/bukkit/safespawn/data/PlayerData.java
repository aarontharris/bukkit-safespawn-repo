package com.ath.bukkit.safespawn.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.entity.Player;

@Entity
@Table( name = "PlayerData" )
public class PlayerData implements Persisted {
	public static final String NAME = "name";

	public static final String[] SCHEMA = {
			"create table PlayerData" +
					"(" +
					"name TEXT," +
					"firstLogin timestamp," +
					"lastLogin timestamp," +
					"timesLoggedIn INTEGER," +
					"id INTEGER primary key autoincrement" +
					");",
			"CREATE INDEX PlayerData_name_INDEX ON PlayerData (name);"
	};

	@Override
	public String[] getSchema() {
		return SCHEMA;
	}

	@Id
	private int id;

	@Column( name = "name", unique = true )
	private String name;

	@Column( name = "firstLogin" )
	private Date firstLogin;

	@Column( name = "lastLogin" )
	private Date lastLogin;

	@Column( name = "timesLoggedIn" )
	private int timesLoggedIn = 0;

	public PlayerData() {
	}

	public static PlayerData newPlayerData( Player player ) {
		PlayerData out = new PlayerData();
		out.setName( player.getName() );
		out.setFirstLogin( new Date() );
		out.setLastLogin( out.getFirstLogin() );
		return out;
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

}
