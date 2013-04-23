package com.ath.bukkit.safespawn.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.avaje.ebean.EbeanServer;

public class BlockStore {

	private EbeanServer database;
	private Map<String, BlockData> dataCache = new HashMap<String, BlockData>();

	public BlockStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		SafeSpawn.logError( e );
	}

	public void syncAll() {
		try {
			SafeSpawn.logLine( "syncAll" );
			Set<String> keys = new HashSet<String>( dataCache.keySet() ); // copy to reduce concurrent mod
			for ( String hash : keys ) {
				BlockData data = dataCache.get( hash );
				if ( data != null && data.isModified() ) {
					SafeSpawn.logLine( "syncAll - " + hash + " id= " + data.getId() );
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
				if ( data == null ) {
					data = dbFind( hash );
				}
				if ( data != null ) {
					dataCache.remove( hash );
					if ( data.getId() > 0 ) { // if persisted
						SafeSpawn.logLine( "dbDelete( " + data.getHash() + " )" );
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
			SafeSpawn.logLine( "dbFind( " + blockHash + " )" );
			BlockData data = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockHash ).findUnique();
			return data;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	/** try cache then db */
	public BlockData getBlockData( String blockHash ) {
		try {
			if ( dataCache.containsKey( blockHash ) ) {
				return dataCache.get( blockHash );
			} else {
				BlockData data = dbFind( blockHash );
				dataCache.put( blockHash, null ); // if its still null, cache it anyway to reduce future reads
				return data;
			}
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
						SafeSpawn.logLine( "dbSave( " + blockData.getHash() + " )" );
						database.save( blockData );
					} catch ( Exception e ) {
						SafeSpawn.logLine( " - BlockData.hash collision on save, updating instead" );
						database.update( blockData );
					}
				} else {
					SafeSpawn.logLine( "dbUpdate( " + blockData.getHash() + " )" );
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
