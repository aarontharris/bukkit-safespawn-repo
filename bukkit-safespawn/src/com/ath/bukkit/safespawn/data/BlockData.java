package com.ath.bukkit.safespawn.data;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@Entity
@Table( name = "BlockData" )
public class BlockData implements Persisted {
	public static final String HASH = "hash";

	public static final String[] SCHEMA = {
			"create table BlockData" +
					"(" +
					"hash TEXT," +
					"meta BLOB," +
					"lastModified bigint," +
					"id INTEGER primary key autoincrement" +
					");",
			"CREATE INDEX BlockData_hash_INDEX ON BlockData (hash);"
	};

	public static final String toHash( Block block ) {
		if ( block != null ) {
			return toHash( block.getLocation(), block.getType() );
		}
		return null;
	}

	public static final String toHash( Location l, Material m ) {
		try {
			if ( l != null && m != null ) {
				return "Block(" + Functions.toString( l ) + ",mat=" + m.getId() + ")";
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	public static BlockData newBlockData( Block block ) {
		BlockData out = new BlockData();
		out.setHash( toHash( block ) );
		out.setLastModified( System.currentTimeMillis() );
		return out;
	}

	/** not null - but will create if doesnt exist */
	public static BlockData attain( Block block ) {
		return SafeSpawn.instance().getBlockStore().attainBlockData( block );
	}

	/** can be null */
	public static BlockData get( Block block ) {
		return SafeSpawn.instance().getBlockStore().getBlockData( toHash( block ) );
	}

	/** can be null */
	public static BlockData get( Location l, Material m ) {
		return SafeSpawn.instance().getBlockStore().getBlockData( toHash( l, m ) );
	}

	@Id
	private int id;

	@Column( name = "lastModified", updatable = true )
	private long lastModified;

	@Column( name = "hash", unique = true, updatable = false )
	private String hash;

	@Column( name = "meta", updatable = true )
	private String meta;

	private transient JSONObject metaJSON;

	private transient boolean modified;

	public BlockData() {
	}

	@Override
	public String toString() {
		return String.format( "BlockData: id=%s,lastModified=%s,hash=%s,meta=%s", id, lastModified, hash, meta );
	};

	@Override
	public String[] getSchema() {
		return SCHEMA;
	}

	public void putStringArray( String key, List<String> strings ) {
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

	/** @deprecated - used by ORM */
	public String getMeta() {
		this.meta = getMetaJSON().toString();
		return this.meta;
	}

	/** @deprecated - used by ORM */
	public void setMeta( String meta ) {
		try {
			if ( meta == null || meta.isEmpty() ) {
				meta = "{}";
			}

			JSONParser parser = new JSONParser();
			this.meta = meta;
			this.metaJSON = (JSONObject) parser.parse( meta );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	private JSONObject getMetaJSON() {
		if ( metaJSON == null ) {
			metaJSON = new JSONObject();
		}
		return metaJSON;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash( String hash ) {
		this.hash = hash;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified( long lastModified ) {
		this.lastModified = lastModified;
	}

	boolean isModified() {
		return modified;
	}

	void setModified( boolean modified ) {
		this.modified = modified;
		setLastModified( System.currentTimeMillis() );
	}
}
