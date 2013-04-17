package com.ath.bukkit.safespawn.data;

import java.util.Date;

import org.json.simple.JSONObject;

public class JSUtl {

	private JSONObject json;

	public JSUtl( JSONObject json ) {
		this.json = json;
		if ( json == null ) {
			throw new NullPointerException( "json cannot be null" );
		}
	}

	public JSONObject getJSON() {
		return json;
	}

	@SuppressWarnings( "unchecked" )
	public void put( String key, String value ) {
		json.put( key, value );
	}

	public String getString( String key, String defaultValue ) {
		String out = (String) json.get( key );
		if ( out != null ) {
			return out;
		}
		return defaultValue;
	}

	@SuppressWarnings( "unchecked" )
	public void put( String key, Date value ) {
		if ( value != null ) {
			json.put( key, value.getTime() );
		}
	}

	public Date getDate( String key, Date defaultValue ) {
		Long time = (Long) json.get( key );
		if ( time != null ) {
			Date out = new Date( time );
			return out;
		}
		return defaultValue;
	}

	@SuppressWarnings( "unchecked" )
	public void put( String key, int value ) {
		json.put( key, String.valueOf( value ) );
	}

	public Integer getInteger( String key, Integer defaultValue ) {
		String intStr = (String) json.get( key );
		if ( intStr != null ) {
			return Integer.parseInt( intStr );
		}
		return defaultValue;
	}
}
