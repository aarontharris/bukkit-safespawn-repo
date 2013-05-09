package com.ath.bukkit.safespawn.data;

import org.bukkit.Chunk;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;

public class ChunkBlocksUnloader implements Task {

	private Chunk chunk;
	private BlockStore store;

	public ChunkBlocksUnloader( Chunk chunk ) {
		this.chunk = chunk;
		this.store = SafeSpawn.instance().getBlockStore();
	}

	// Unload blocks
	@Override
	public void run() {
		try {
			// Log.line( "Running ChunkBlocksUnloader for %sx%s", chunk.getX(), chunk.getZ() );
			// find all blocks for this chunk
			// for each block
			// - remove from cache
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

}
