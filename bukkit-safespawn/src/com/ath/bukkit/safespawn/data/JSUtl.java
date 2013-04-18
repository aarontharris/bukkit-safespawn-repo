package com.ath.bukkit.safespawn.data;

import java.util.Date;

import org.bukkit.Location;
import org.json.simple.JSONArray;
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

	public void put( String key, Location l ) {
		put( key, new LocationSerializer( l ) );
	}

	public Location getLocation( String key, Location defaultValue ) {
		try {
			LocationSerializer ser = new LocationSerializer();
			getJSer( key, ser );
			if ( ser.getLocation() != null ) {
				return ser.getLocation();
			}
		} catch ( Exception e ) {
		}
		return defaultValue;
	}

	@SuppressWarnings( "unchecked" )
	public void put( String key, JSONSerializable jser ) {
		try {
			if ( jser != null ) {
				json.put( key, jser.toJson() );
			}
		} catch ( Exception e ) {
		}
	}

	public void getJSer( String key, JSONSerializable jser ) {
		if ( jser != null ) {
			try {
				jser.fromJson( (JSONObject) json.get( key ) );
			} catch ( Exception e ) {
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	public void put( String key, JSONArray array ) {
		json.put( key, array );
	}

	public JSONArray getJSONArray( String key, JSONArray defaultValue ) {
		Object o = json.get( key );
		if ( o != null && o instanceof JSONArray ) {
			return (JSONArray) o;
		}
		return defaultValue;
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

	@SuppressWarnings( "unchecked" )
	public void put( String key, Float value ) {
		json.put( key, String.valueOf( value ) );
	}

	public Float getFloat( String key, Float defaultValue ) {
		String str = (String) json.get( key );
		if ( str != null ) {
			return Float.parseFloat( str );
		}
		return defaultValue;
	}

	@SuppressWarnings( "unchecked" )
	public void put( String key, Double value ) {
		json.put( key, String.valueOf( value ) );
	}

	public Double getDouble( String key, Double defaultValue ) {
		String doubleStr = (String) json.get( key );
		if ( doubleStr != null ) {
			return Double.parseDouble( doubleStr );
		}
		return defaultValue;
	}
}
