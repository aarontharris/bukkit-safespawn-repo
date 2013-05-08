package com.ath.bukkit.safespawn.data;

import java.util.Collection;
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
	public static final String CHUNK_X = "chunk_x";
	public static final String CHUNK_Z = "chunk_z";

	public static final String[] SCHEMA = {
			"create table BlockData" +
					"(" +
					"hash TEXT," +
					"meta BLOB," +
					"lastModified bigint," +
					"chunk_x INTEGER," +
					"chunk_z INTEGER," +
					"block_x INTEGER," +
					"block_y INTEGER," +
					"block_z INTEGER," +
					"block_w TEXT," +
					"block_m INTEGER," +
					"id INTEGER primary key autoincrement" +
					");",
			"CREATE INDEX BlockData_hash_INDEX ON BlockData (hash);",
			"CREATE INDEX BlockData_chkx_INDEX ON BlockData (chunk_x);",
			"CREATE INDEX BlockData_chkz_INDEX ON BlockData (chunk_z);"
	};

	/*
	 * create table temp as select hash, meta, lastModified, id from BlockData;
	 * -
	 * insert into BlockData ( hash, meta, lastModified, id )
	 * select hash, meta, lastModified, id
	 * from temp;
	 */

	private static final String WRITE_ACCESS = "write_access";
	private static final String READ_ACCESS = "read_access";

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
		out.setChunkX( block.getChunk().getX() );
		out.setChunkZ( block.getChunk().getZ() );
		out.setBlockX( block.getX() );
		out.setBlockY( block.getY() );
		out.setBlockZ( block.getZ() );
		out.setBlockW( block.getLocation().getWorld().getName() );
		out.setBlockM( block.getTypeId() );
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

	@Column( name = "chunk_x", updatable = false )
	private int chunkX;

	@Column( name = "chunk_z", updatable = false )
	private int chunkZ;

	@Column( name = "block_x", updatable = false )
	private int blockX;

	@Column( name = "block_y", updatable = false )
	private int blockY;

	@Column( name = "block_z", updatable = false )
	private int blockZ;

	@Column( name = "block_w", updatable = false )
	private String blockW;

	@Column( name = "block_m", updatable = false )
	private int blockM;

	@Column( name = "hash", unique = true, updatable = false )
	private String hash;

	@Column( name = "meta", updatable = true )
	private String meta;

	@Column( name = "lastModified", updatable = true )
	private long lastModified;

	private transient JSONObject metaJSON;

	private transient boolean modified;

	private transient Set<String> rAccess;
	private transient Set<String> wAccess;

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

	public Set<String> getReadAccess() {
		if ( rAccess == null ) {
			rAccess = getStringSet( READ_ACCESS );
		}
		return rAccess;
	}

	public void setReadAccess( Set<String> access ) {
		if ( access == null ) {
			rAccess = null;
			removeKey( READ_ACCESS );
		} else {
			rAccess = Sets.newHashSet( access );
			putStringCollection( READ_ACCESS, rAccess );
		}
	}

	public Set<String> getWriteAccess() {
		if ( wAccess == null ) {
			wAccess = getStringSet( WRITE_ACCESS );
		}
		return wAccess;
	}

	public void setWriteAccess( Set<String> access ) {
		if ( access == null ) {
			wAccess = null;
			removeKey( WRITE_ACCESS );
		} else {
			rAccess = Sets.newHashSet( access );
			putStringCollection( WRITE_ACCESS, wAccess );
		}
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

	public int getChunkX() {
		return chunkX;
	}

	/** @deprecated only used by ORM */
	public void setChunkX( int chunkX ) {
		this.chunkX = chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	/** @deprecated only used by ORM */
	public void setChunkZ( int chunkZ ) {
		this.chunkZ = chunkZ;
	}

	public int getBlockX() {
		return blockX;
	}

	public void setBlockX( int blockX ) {
		this.blockX = blockX;
	}

	public int getBlockY() {
		return blockY;
	}

	public void setBlockY( int blockY ) {
		this.blockY = blockY;
	}

	public int getBlockZ() {
		return blockZ;
	}

	public void setBlockZ( int blockZ ) {
		this.blockZ = blockZ;
	}

	public String getBlockW() {
		return blockW;
	}

	public void setBlockW( String blockW ) {
		this.blockW = blockW;
	}

	public int getBlockM() {
		return blockM;
	}

	public void setBlockM( int blockM ) {
		this.blockM = blockM;
	}
}
