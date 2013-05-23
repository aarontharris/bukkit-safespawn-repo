package com.ath.bukkit.safespawn.data;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.google.common.collect.Sets;


@Entity
@Table( name = "BlockData" )
public class BlockData extends MetaData implements Persisted {
	public static final String HASH = "hash";
	public static final String BLOCK_X = "block_x";
	public static final String BLOCK_Y = "block_y";
	public static final String BLOCK_Z = "block_z";
	public static final String BLOCK_W = "block_w";
	public static final String CHUNK_X = "chunk_x";
	public static final String CHUNK_Z = "chunk_z";
	public static final String REF = "ref";

	public static final String[] SCHEMA = {
			"create table BlockData" +
					"(" +
					"hash TEXT," +
					"meta BLOB," +
					"ref TEXT," +
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

	// alter table BlockData add column ref text;

	/*
	 * create table temp as select hash, meta, lastModified, id from BlockData;
	 * -
	 * insert into BlockData ( hash, meta, lastModified, id )
	 * select hash, meta, lastModified, id
	 * from temp;
	 */

	private static final String WRITE_ACCESS = "write_access";
	private static final String READ_ACCESS = "read_access";
	private static final String OWNER = "owner";
	private static final String NO_OWNER = "-"; // for improved caching

	public static final String toHash( Block block ) {
		if ( block != null ) {
			return toHash( block.getLocation(), block.getType() );
		}
		return null;
	}

	public static final String toHash( Location l, Material m ) {
		try {
			if ( l != null && m != null ) {
				return "Block(" + F.toString( l ) + ",mat=" + m.getId() + ")";
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
		out.setModified( true );
		return out;
	}

	public static void save( BlockData data ) {
		try {
			SafeSpawn.instance().getBlockStore().saveBlockData( data );
			data.setModified( false );
		} catch ( Exception e ) {
			Log.error( e );
		}
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

	@Column( name = "ref", updatable = true )
	private String ref;

	@Column( name = "lastModified", updatable = true )
	private long lastModified;

	private transient String owner;
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

	// public Set<String> getReadAccess() {
	// if ( rAccess == null ) {
	// rAccess = getStringSet( READ_ACCESS );
	// }
	// return rAccess;
	// }
	//
	// public void setReadAccess( Set<String> access ) {
	// if ( access == null ) {
	// rAccess = null;
	// removeKey( READ_ACCESS );
	// } else {
	// rAccess = Sets.newHashSet( access );
	// putStringCollection( READ_ACCESS, rAccess );
	// }
	// }

	public Set<String> getAccess() {
		if ( wAccess == null ) {
			wAccess = getStringSet( WRITE_ACCESS );
		}
		return wAccess;
	}

	public void setAccess( Set<String> access ) {
		if ( access == null ) {
			wAccess = null;
			removeKey( WRITE_ACCESS );
		} else {
			wAccess = Sets.newHashSet( access );
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
			setMetaJSON( (JSONObject) parser.parse( meta ) );
		} catch ( Exception e ) {
			Log.error( e );
		}
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

	/** @deprecated use setModified */
	@Override
	public void setLastModified( long lastModified ) {
		this.lastModified = lastModified;
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

	public String getOwner() {
		if ( owner == null ) {
			owner = getString( OWNER, NO_OWNER );
		}
		if ( NO_OWNER == owner ) { // our sentinal to keep from digging out of the dictionary
			return null;
		}
		return owner;
	}

	public void setOwner( String owner ) {
		this.owner = owner;
		putString( OWNER, owner );
		this.setModified( true );
	}

	public String getRef() {
		return ref;
	}

	/** @deprecated don't use this, its only for ORM */
	public void setRef( String ref ) {
		this.ref = ref;
	}
	
	public void updateRef( String ref ) {
		this.ref = ref;
		this.setModified( true );
	}

}
