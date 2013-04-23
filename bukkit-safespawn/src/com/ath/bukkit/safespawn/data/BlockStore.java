package com.ath.bukkit.safespawn.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.avaje.ebean.EbeanServer;

public class BlockStore {

	// FIXME: blockStore will be too frequent... needs to be in-memory with timed syncs

	private EbeanServer database;
	private Map<String, BlockData> dataCache = new HashMap<String, BlockData>();

	public BlockStore( EbeanServer database ) {
		this.database = database;
	}

	public void logError( Exception e ) {
		SafeSpawn.logError( e );
	}

	/** when the block is destroyed */
	public void remove( Block block ) {
		try {
			if ( block != null ) {
				String hash = BlockData.toHash( block );
				BlockData data = dataCache.get( hash );
				if ( data == null ) {
					data = getBlockDataByBlockHash( hash );
				}
				if ( data != null ) {
					dataCache.remove( data );
					database.delete( data );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public BlockData getBlockData( Block block ) {
		try {
			if ( block != null ) {
				String hash = BlockData.toHash( block );
				BlockData data = dataCache.get( hash );
				if ( data == null ) {
					data = getBlockDataByBlockHash( hash );
					if ( data != null ) {
						dataCache.put( hash, data );
					}
				}
				return data;
			}
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	public void saveBlockData( BlockData blockData ) {
		try {
			if ( blockData != null ) {
				dataCache.put( blockData.getHash(), blockData );
				saveBlockDataToDb( blockData );
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public BlockData attainBlockData( Block block ) {
		try {
			String hash = BlockData.toHash( block );
			BlockData data = getBlockDataByBlockHash( hash );
			if ( data == null ) {
				data = BlockData.newBlockData( block );
			}
			return data;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private BlockData getBlockDataByBlock( Block block ) {
		try {
			if ( block != null ) {
				return getBlockDataByBlockHash( BlockData.toHash( block ) );
			}
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private BlockData getBlockDataByBlockHash( String blockHash ) {
		try {
			BlockData out = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockHash ).findUnique();
			return out;
		} catch ( Exception e ) {
			logError( e );
		}
		return null;
	}

	private void saveBlockDataToDb( BlockData blockData ) {
		try {
			if ( blockData != null ) {
				BlockData data = database.find( BlockData.class ).where().ieq( BlockData.HASH, blockData.getHash() ).findUnique();
				if ( data != null ) {
					database.update( blockData );
				} else {
					database.save( blockData );
				}
			}
		} catch ( Exception e ) {
			logError( e );
		}
	}

	public boolean isMagical( Block block ) {
		try {
			if ( block != null ) {
				BlockData data = getBlockDataByBlock( block );
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
