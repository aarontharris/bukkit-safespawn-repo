package com.ath.bukkit.safespawn.data;

import java.util.Set;

import org.bukkit.Chunk;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;

public class ChunkBlocksLoader implements Task {

	private Chunk chunk;
	private BlockStore store;

	public ChunkBlocksLoader( Chunk chunk ) {
		this.chunk = chunk;
		this.store = SafeSpawn.instance().getBlockStore();
	}

	// Load blocks
	@Override
	public void run() {
		try {
			Set<BlockData> blocks = store.dbFindAllNearBy( chunk );
			for ( BlockData bd : blocks ) {
				store.cacheBlockData( bd );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}

		// do in series, not parallel
		// ChunkBlocksGC gc = new ChunkBlocksGC( chunk ); gc.run();
	}

}
