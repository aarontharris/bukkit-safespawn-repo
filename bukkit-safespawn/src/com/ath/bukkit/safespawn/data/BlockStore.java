package com.ath.bukkit.safespawn.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlUpdate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BlockStore {

	private EbeanServer database;

	private Map<String, BlockData> dataCache = Maps.newConcurrentMap();
	private Map<String, Set<BlockData>> chunkToBlockHashes = Maps.newConcurrentMap();

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
				if ( data != null ) {
					try {
						World w = SafeSpawn.instance().getServer().getWorld( data.getBlockW() );
						Block block = w.getBlockAt( data.getBlockX(), data.getBlockY(), data.getBlockZ() );
						if ( !BlockData.toHash( block ).equals( data.getHash() ) ) {
							Log.line( "syncAll - remove " + hash + " id= " + data.getId() );
							remove( data );
						} else if ( data.isModified() ) {
							Log.line( "syncAll - saving " + hash + " id= " + data.getId() );
							BlockData.save( data );
						}
					} catch ( Exception e ) {
						Log.error( e );
					}
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
				remove( data );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	/** when the block is destroyed */
	public void remove( BlockData data ) {
		try {
			if ( data != null ) {
				removeFromCache( data );
				if ( data.getId() > 0 ) { // if persisted
					deleteBlockData( data );
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

	public Set<BlockData> dbFindByRef( String ref ) {
		try {
			Set<BlockData> data = database.find( BlockData.class ).where().ieq( BlockData.REF, ref ).findSet();
			return data;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** pull straight from db */
	@SuppressWarnings( { "deprecation", "unused" } )
	private BlockData dbFind( String blockHash ) {
		try {
			Log.line( "dbFind( " + blockHash + " )" );
			BlockData data = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockHash ).findUnique();
			data.setMeta( F.fromDbSafeString( data.getMeta() ) );
			return data;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	public Set<BlockData> dbFind( int x, int y, int z, World w ) {
		try {
			String queryString = String.format( "find BlockData where %s=:%s and %s=:%s and %s=:%s and %s=:%s",
					BlockData.BLOCK_W, BlockData.BLOCK_W,
					BlockData.BLOCK_X, BlockData.BLOCK_X,
					BlockData.BLOCK_Y, BlockData.BLOCK_Y,
					BlockData.BLOCK_Z, BlockData.BLOCK_Z
					);
			Query<BlockData> query = database.createQuery( BlockData.class, queryString );
			query.setParameter( BlockData.BLOCK_W, w.getName() );
			query.setParameter( BlockData.BLOCK_X, x );
			query.setParameter( BlockData.BLOCK_Y, y );
			query.setParameter( BlockData.BLOCK_Z, z );
			return query.findSet();
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

	@SuppressWarnings( "deprecation" )
	void saveBlockData( BlockData blockData ) {
		try {
			if ( blockData != null ) {
				if ( blockData.getId() <= 0 ) {
					try {
						Log.line( "dbSave( " + blockData.getHash() + " )" );
						String meta = F.toDbSafeString( blockData.getMeta() );
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

	/** straight up save to db, no fuss */
	public void dbSave( BlockData bd ) {
		try {
			database.save( bd );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/** expects the blockdata to have an Id > 0 */
	private void updateBlockData( BlockData blockData ) {
		try {
			if ( blockData != null ) {
				Log.line( "dbUpdate( " + blockData.toString() + " )" );
				// database.update( blockData ); // broken for some reason :(
				@SuppressWarnings( "deprecation" )
				String meta = blockData.getMeta();
				Log.line( "BEFORE %S", meta );
				meta = F.toDbSafeString( meta );
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
