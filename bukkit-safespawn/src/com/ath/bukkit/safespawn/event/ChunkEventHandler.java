package com.ath.bukkit.safespawn.event;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;

public class ChunkEventHandler {

	public static void onChunkLoadEvent( SafeSpawn plugin, ChunkLoadEvent event ) {
		try {
			Log.line( "onChunkLoadEvent" );
			// plugin.getBlockStore().onChunkLoad( event.getChunk() );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onChunkUnloadEvent( SafeSpawn plugin, ChunkUnloadEvent event ) {
		try {
			Log.line( "onChunkUnloadEvent" );
			// plugin.getBlockStore().onChunkUnload( event.getChunk() );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onChunkPopulateEvent( SafeSpawn plugin, ChunkPopulateEvent event ) {
		try {
			Log.line( "onChunkPopulateEvent" );
			// plugin.getBlockStore().onChunkLoad( event.getChunk() );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

}
