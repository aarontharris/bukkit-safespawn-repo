package com.ath.bukkit.safespawn.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.Log;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlUpdate;

public class BlockStore {

	private EbeanServer database;
	private Map<String, BlockData> dataCache = new HashMap<String, BlockData>(); // FIXME: make me a WeakHashMap

	public BlockStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		Log.error( e );
	}

	public void primeTheCache() { // TODO break this down into onChunkLoad?
		try {
			// BlockData data = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockHash ).findUnique();
			Log.line( "dbFind( * )" );
			Set<BlockData> all = dbFindAll();
			for ( BlockData data : all ) {
				dataCache.put( data.getHash(), data );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public void syncAll() {
		try {
			Log.line( "syncAll" );
			Set<String> keys = new HashSet<String>( dataCache.keySet() ); // copy to reduce concurrent mod
			for ( String hash : keys ) {
				BlockData data = dataCache.get( hash );
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
				// if ( data == null ) {
				// data = dbFind( hash );
				// }
				if ( data != null ) {
					dataCache.remove( hash );
					if ( data.getId() > 0 ) { // if persisted
						Log.line( "dbDelete( " + data.getHash() + " )" );
						database.delete( data );
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
				dataCache.put( hash, data );
			}
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

	/** try cache -- if its not in cache, its not in db */
	public BlockData getBlockData( String blockHash ) {
		try {
			// if ( dataCache.containsKey( blockHash ) ) {
			// return dataCache.get( blockHash );
			// } else {
			// BlockData data = dbFind( blockHash );
			// dataCache.put( blockHash, null ); // if its still null, cache it anyway to reduce future reads
			// return data;
			// }
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

}
