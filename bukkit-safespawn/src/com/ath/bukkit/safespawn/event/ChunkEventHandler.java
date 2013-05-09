package com.ath.bukkit.safespawn.event;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.ChunkBlocksLoader;
import com.ath.bukkit.safespawn.data.ChunkBlocksUnloader;

public class ChunkEventHandler {

	public static void onChunkLoadEvent( SafeSpawn plugin, ChunkLoadEvent event ) {
		try {
			// plugin.getTaskman().addNonRepeatingTask( new ChunkBlocksLoader( event.getChunk() ) );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onChunkUnloadEvent( SafeSpawn plugin, ChunkUnloadEvent event ) {
		try {
			// plugin.getTaskman().addNonRepeatingTask( new ChunkBlocksUnloader( event.getChunk() ) );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

}
