package com.ath.bukkit.safespawn.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlUpdate;
import com.google.common.collect.Sets;

public class BlockStore {

	private EbeanServer database;
	private Map<String, BlockData> dataCache = new HashMap<String, BlockData>(); //
	private Map<String, Set<BlockData>> chunkToBlockHashes = new HashMap<String, Set<BlockData>>();

	// TODO: make sure its the same chunk for each read and not just a copy with the same data which would break the cache
	// TODO: maybe convert back to hash instead of chunk

	// private Queue<BlockData> outgoing = new ConcurrentLinkedQueue<BlockData>();
	// private Queue<BlockData> incoming = new ConcurrentLinkedQueue<BlockData>();

	public BlockStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		Log.error( e );
	}

	/** immutable */
	public Set<String> getCachedChunks() {
		return chunkToBlockHashes.keySet();
	}

	public Collection<BlockData> getCachedBlockDatas() {
		return dataCache.values();
	}

	/** careful its mutable */
	public Set<BlockData> getBlockDatasByChunk( String chunkHash ) {
		try {
			Set<BlockData> data = chunkToBlockHashes.get( chunkHash );
			if ( data == null ) {
				data = Sets.newHashSet();
				chunkToBlockHashes.put( chunkHash, data );
			}
			return data;
		} catch ( Exception e ) {
			Log.error( e );
		}
		Log.line( "WARN WARN WARN - getBlockDatasByChunk returning untracked set for chunk %s", chunkHash );
		return Sets.newHashSet();
	}

	public static String toHash( Chunk chunk ) {
		if ( chunk != null ) {
			return toChunkHash( chunk.getWorld().getName(), chunk.getX(), chunk.getZ() );
		}
		return null;
	}

	public static String toChunkHash( String worldName, int chunkx, int chunkz ) {
		return worldName + "," + chunkx + "," + chunkz;
	}

	public static Chunk toChunk( String hash ) {
		try {
			String[] parts = hash.split( "," );
			String worldName = parts[0];
			int x = Integer.valueOf( parts[1] );
			int z = Integer.valueOf( parts[2] );
			return SafeSpawn.instance().getServer().getWorld( worldName ).getChunkAt( x, z );
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	public void primeTheCache() { // FIXME: make this go away when replaced with chunk load/unload
		try {
			Set<BlockData> all = dbFindAll();
			for ( BlockData data : all ) {
				if ( !cacheBlockData( data ) ) {
					// skip the cache and remove the invalid block
					Log.line( "WARNING: forced to remove %s", data.toString() );
					deleteBlockData( data );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	/** returns true if after this call, the BlockData is in the cache */
	public boolean cacheBlockData( BlockData data ) {
		try {
			if ( data != null ) {
				BlockData exists = getBlockData( data.getHash() );
				if ( exists != null ) {
					if ( exists.getLastModified() >= data.getLastModified() ) {
						return true; // bail out - dont overwrite newer copy
					}
				}

				String wName = data.getBlockW();
				int cx = data.getChunkX();
				int cz = data.getChunkZ();
				if ( wName != null && cx != 0 && cz != 0 ) {
					World w = SafeSpawn.instance().getServer().getWorld( wName );
					if ( w != null ) {
						Log.line( "cacheBlockData( %s ) added", data );
						Chunk chunk = w.getChunkAt( cx, cz );
						Set<BlockData> chunkblocks = getBlockDatasByChunk( toHash( chunk ) );
						chunkblocks.add( data );
						dataCache.put( data.getHash(), data );
						return true;
					}
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		Log.line( "cacheBlockData( %s ) not added", data );
		return false;
	}

	public void removeFromCache( BlockData data ) {
		try {
			if ( data != null ) {
				String wName = data.getBlockW();
				int cx = data.getChunkX();
				int cz = data.getChunkZ();
				if ( wName != null && cx != 0 && cz != 0 ) {
					World w = SafeSpawn.instance().getServer().getWorld( wName );
					if ( w != null ) {
						Chunk chunk = w.getChunkAt( cx, cz );
						getBlockDatasByChunk( toHash( chunk ) ).remove( data );
					}
				}
				Log.line( "removeFromCache( %s )", data );
				dataCache.remove( data.getHash() );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public void removeFromCache( String chunkHash ) {
		try {
			Set<BlockData> datas = getBlockDatasByChunk( chunkHash );
			for ( BlockData data : datas ) {
				Log.line( "removeFromCache( %s )", data );
				dataCache.remove( data.getHash() );
			}
			datas.clear();
			chunkToBlockHashes.remove( chunkHash );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public void syncAll() {
		try {
			Set<String> keys = new HashSet<String>( dataCache.keySet() ); // copy to reduce concurrent mod
			for ( String hash : keys ) {
				BlockData data = getBlockData( hash );
				if ( data != null && data.isModified() ) {
					Log.line( "syncAll - " + hash + " id= " + data.getId() );
					saveBlockData( data );
					data.setModified( false );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	/** when the block is destroyed */
	public void remove( Block block ) {
		try {
			if ( block != null ) {
				String hash = BlockData.toHash( block );
				BlockData data = getBlockData( hash );
				if ( data != null ) {
					removeFromCache( data );
					if ( data.getId() > 0 ) { // if persisted
						deleteBlockData( data );
					}
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	/** try cache, then db, else create new but not saved to db */
	public BlockData attainBlockData( Block block ) {
		try {
			String hash = BlockData.toHash( block );
			BlockData data = getBlockData( hash );
			if ( data == null ) {
				data = BlockData.newBlockData( block );
			}
			cacheBlockData( data );
			return data;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	/** pull straight from db */
	@SuppressWarnings( { "deprecation", "unused" } )
	private BlockData dbFind( String blockHash ) {
		try {
			Log.line( "dbFind( " + blockHash + " )" );
			BlockData data = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockHash ).findUnique();
			data.setMeta( Functions.fromDbSafeString( data.getMeta() ) );
			return data;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	@SuppressWarnings( "deprecation" )
	private Set<BlockData> dbFindAll() {
		try {
			Log.line( "dbFind( * )" );
			Set<BlockData> all = database.find( BlockData.class ).findSet();
			for ( BlockData data : all ) {
				data.setMeta( Functions.fromDbSafeString( data.getMeta() ) );
			}
			return all;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	/** careful - kinda heavy */
	public Set<BlockData> dbFindAll( Chunk chunk ) {
		try {
			int cx = chunk.getX();
			int cz = chunk.getZ();

			Log.line( "dbFindAll( chunk=%s, %s )", cx, cz );
			String queryString = "find BlockData where chunk_x=:chunk_x and chunk_z=:chunk_z and block_w=:block_w";
			Query<BlockData> query = database.createQuery( BlockData.class, queryString );
			query.setParameter( "chunk_x", cx );
			query.setParameter( "chunk_z", cz );
			query.setParameter( "block_w", chunk.getWorld().getName() );
			return query.findSet();
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	public Set<BlockData> dbFindAllNearBy( Chunk chunk ) {
		try {
			int cx = chunk.getX();
			int cz = chunk.getZ();

			Log.line( "dbFindAllNearBy( chunk=%s, %s )", cx, cz );
			// String queryString = "find BlockData where chunk_x=:chunk_x and chunk_z=:chunk_z and block_w=:block_w";
			Query<BlockData> query = database.createQuery( BlockData.class ).where()
					.between( "chunk_x", ( cx - 1 ), ( cx + 1 ) )
					.between( "chunk_z", ( cz - 1 ), ( cz + 1 ) )
					.query();
			// query.setParameter( "chunk_x", cx );
			// query.setParameter( "chunk_z", cz );
			// query.setParameter( "block_w", chunk.getWorld().getName() );
			return query.findSet();
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	/** try cache -- if its not in cache, its not in db */
	public BlockData getBlockData( String blockHash ) {
		try {
			return dataCache.get( blockHash );
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private void saveBlockData( BlockData blockData ) {
		try {
			if ( blockData != null ) {
				if ( blockData.getId() <= 0 ) {
					try {
						Log.line( "dbSave( " + blockData.getHash() + " )" );
						@SuppressWarnings( "unused" )
						String meta = Functions.toDbSafeString( blockData.getMeta() );
						blockData.setMeta( meta );
						database.save( blockData );
					} catch ( Exception e ) {
						Log.line( " - BlockData.hash collision on save, updating instead" );
						updateBlockData( blockData );
					}
				} else {
					updateBlockData( blockData );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	/** expects the blockdata to have an Id > 0 */
	private void updateBlockData( BlockData blockData ) {
		try {
			if ( blockData != null ) {
				Log.line( "dbUpdate( " + blockData.toString() + " )" );
				// database.update( blockData ); // broken for some reason :(
				String meta = blockData.getMeta();
				Log.line( "BEFORE %S", meta );
				meta = Functions.toDbSafeString( meta );
				Log.line( "AFTER  %S", meta );
				String update = String.format( "update BlockData set lastModified=%s, meta='%s' where id=%s", blockData.getLastModified(), meta, blockData.getId() );
				Log.line( update );
				SqlUpdate sqlUpdate = database.createSqlUpdate( update );
				database.execute( sqlUpdate );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** expects the blockdata to have an Id > 0 */
	private void deleteBlockData( BlockData blockData ) {
		try {
			Log.line( "dbDelete( %s )", blockData );
			String update = String.format( "delete from BlockData where id=%s", blockData.getId() );
			SqlUpdate sqlUpdate = database.createSqlUpdate( update );
			database.execute( sqlUpdate );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
