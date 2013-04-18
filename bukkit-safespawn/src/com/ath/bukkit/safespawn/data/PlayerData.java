package com.ath.bukkit.safespawn.data;

import java.util.Date;

import org.json.simple.JSONObject;

public class PlayerData implements JSONSerializable {

	private String name;
	private Date firstLogin;
	private Date lastLogin;
	private int timesLoggedIn = 0;

	public PlayerData() {
	}

	public JSONObject toJson() throws Exception {
		JSUtl js = new JSUtl( new JSONObject() );
		js.put( "name", name );
		js.put( "firstLogin", firstLogin );
		js.put( "lastLogin", lastLogin );
		js.put( "timesLoggedIn", timesLoggedIn );
		return js.getJSON();
	}

	@Override
	public void fromJson( JSONObject json ) throws Exception {
		JSUtl js = new JSUtl( json );
		name = js.getString( "name", null );
		firstLogin = js.getDate( "firstLogin", null );
		lastLogin = js.getDate( "lastLogin", null );
		timesLoggedIn = js.getInteger( "timesLoggedIn", 0 );
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

}
