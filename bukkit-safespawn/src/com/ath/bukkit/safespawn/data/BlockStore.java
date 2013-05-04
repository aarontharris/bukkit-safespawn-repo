package com.ath.bukkit.safespawn.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;

import com.ath.bukkit.safespawn.Log;
import com.avaje.ebean.EbeanServer;

public class BlockStore {

	private EbeanServer database;
	private Map<String, BlockData> dataCache = new HashMap<String, BlockData>();

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
			Set<BlockData> all = database.find( BlockData.class ).findSet();
			for ( BlockData data : all ) {
				Log.line( " - Found: " + data.getHash() );
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
	private BlockData dbFind( String blockHash ) {
		try {
			Log.line( "dbFind( " + blockHash + " )" );
			BlockData data = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockHash ).findUnique();
			return data;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
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
						database.save( blockData );
					} catch ( Exception e ) {
						Log.line( " - BlockData.hash collision on save, updating instead" );
						database.update( blockData );
					}
				} else {
					Log.line( "dbUpdate( " + blockData.getHash() + " )" );
					database.update( blockData );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public boolean isMagical( Block block ) {
		try {
			if ( block != null ) {
				BlockData data = getBlockData( BlockData.toHash( block ) );
				if ( data != null && data.isMagical() ) {
					return true;
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
		return false;
	}

}
