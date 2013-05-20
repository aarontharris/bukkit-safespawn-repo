package com.ath.bukkit.safespawn.data;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ath.bukkit.safespawn.Log;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class MetaData {

	private transient boolean modified;

	private transient JSONObject metaJSON;

	public abstract void setLastModified( long millis );

	protected JSONObject getMetaJSON() {
		if ( metaJSON == null ) {
			metaJSON = new JSONObject();
		}
		return metaJSON;
	}

	protected void setMetaJSON( JSONObject json ) {
		this.metaJSON = json;
	}

	public boolean isModified() {
		return modified;
	}

	void setModified( boolean modified ) {
		this.modified = modified;
		setLastModified( System.currentTimeMillis() );
	}

	public void putStringCollection( String key, Collection<String> strings ) {
		try {
			JSONArray ary = new JSONArray();
			ary.addAll( strings );
			getMetaJSON().put( key, ary );
			setModified( true );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** never null */
	public Set<String> getStringSet( String key ) {
		Set<String> out = Sets.newHashSet( getStringArray( key ) );
		return out;
	}

	/** never null */
	public List<String> getStringArray( String key ) {
		Log.line( "getStringArray( %s )", key );
		List<String> out = Lists.newArrayList();
		try {
			JSONArray ary = (JSONArray) getMetaJSON().get( key );
			if ( ary != null ) {
				for ( Object o : ary.toArray() ) {
					out.add( o.toString() );
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return out;
	}

	public void removeKey( String key ) {
		try {
			getMetaJSON().remove( key );
			setModified( true );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public void putString( String key, String value ) {
		try {
			getMetaJSON().put( key, value );
			setModified( true );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public String getString( String key, String defaultValue ) {
		try {
			String out = (String) getMetaJSON().get( key );
			if ( out != null ) {
				return out;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return defaultValue;
	}

	public void putBoolean( String key, boolean value ) {
		putString( key, String.valueOf( value ) );
	}

	public boolean getBoolean( String key, boolean defaultValue ) {
		try {
			String strVal = getString( key, null );
			if ( strVal != null ) {
				return Boolean.valueOf( strVal );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return defaultValue;
	}

	public void putInt( String key, int value ) {
		putString( key, String.valueOf( value ) );
	}

	public int getInt( String key, int defaultValue ) {
		try {
			String strVal = getString( key, null );
			if ( strVal != null ) {
				return Integer.valueOf( strVal );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return defaultValue;
	}

}
