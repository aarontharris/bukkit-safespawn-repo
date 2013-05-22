package com.ath.bukkit.safespawn.data;

import java.util.Set;

import org.bukkit.Chunk;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;

public class ChunkBlocksGC implements Task {

	private static final long MIN_RUN_PERIOD_MILLIS = 1000 * 60; // minute

	private static long lastRun = 0;

	private Chunk chunk;
	private BlockStore store;

	public ChunkBlocksGC( Chunk chunk ) {
		this.chunk = chunk;
		this.store = SafeSpawn.instance().getBlockStore();
	}

	// Load blocks
	@Override
	public void run() {
		try {
			long now = System.currentTimeMillis();
			if ( ( now - lastRun ) >= MIN_RUN_PERIOD_MILLIS ) {
				Set<BlockData> blocks = store.getBlockDatasByChunk( BlockStore.toHash( chunk ) );
				for ( BlockData bd : blocks ) {
					if ( bd != null ) {
						// store.remove( chunk.getBlock( ) );
						Set<BlockData> match = store.dbFind( bd.getBlockX(), bd.getBlockY(), bd.getBlockZ(), chunk.getWorld() );
						for ( BlockData m : match ) {
							if ( m != null && m.getHash().equals( bd.getHash() ) ) {
								Log.line( "Found a defunct BlockData %s", bd );
								store.remove( bd );
								break;
							}
						}
					}
				}
				lastRun = now;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
