package com.ath.bukkit.safespawn;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ath.bukkit.safespawn.data.BlockStore;
import com.ath.bukkit.safespawn.data.ChunkBlocksLoader;
import com.ath.bukkit.safespawn.data.PlayerData;
import com.ath.bukkit.safespawn.data.Task;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CacheManager {
	private static boolean initialized = false;

	public static void init( final TaskManager taskmgr ) {
		if ( !initialized ) {
			cleanUpUnusedBlocks( taskmgr );
			savePlayers( taskmgr );
			syncBlocks( taskmgr );
		}
	}

	private static void syncBlocks( final TaskManager taskmgr ) {
		taskmgr.addSlowRepeatingTask( new Task() {
			@Override
			public void run() {
				SafeSpawn.instance().getBlockStore().syncAll();
			}
		} );
	}

	private static void cleanUpUnusedBlocks( final TaskManager taskmgr ) {
		taskmgr.addSlowRepeatingTask( new Task() { // clean up unused blocks slowly
			@Override
			public void run() {
				try {
					// find all active chunks
					Set<String> activeChunks = findActiveChunkHashes( 1 );

					// now clear cache for chunks that are cached but are not found as active above.
					Set<String> chunkKeys = new HashSet<String>( SafeSpawn.instance().getBlockStore().getCachedChunks() );
					for ( String chunkHash : chunkKeys ) {
						if ( activeChunks.contains( chunkHash ) ) {
							continue;
						}

						boolean hasPlayers = false;
						Chunk chunk = BlockStore.toChunk( chunkHash );
						for ( Entity e : chunk.getEntities() ) {
							if ( e instanceof Player ) {
								hasPlayers = true;
								break;
							}
						}

						if ( !hasPlayers ) {
							SafeSpawn.instance().getBlockStore().removeFromCache( chunkHash );
						}
					}
				} catch ( Exception e ) {
					Log.error( e );
				}
			}
		} );

	}

	// private static void deleteMissedMagicBlocks( final TaskManager taskmgr ) {
	// taskmgr.addSlowRepeatingTask( new Task() {
	// @Override
	// public void run() {
	// SafeSpawn.instance().getBlockStore().getBlockData( blockHash )
	// }
	// });
	// }

	private static void savePlayers( final TaskManager taskmgr ) {
		taskmgr.addSlowRepeatingTask( new Task() {
			@Override
			public void run() {
				// loop thru all players in cache
				// for each modified
				// - save
				// for each not online
				// - remove from cache

				for ( PlayerData pd : SafeSpawn.instance().getPlayerStore().getPlayersInCache() ) {
					if ( pd.isModified() ) {
						Log.line( "Saving: %s", pd.getName() );
						PlayerData.save( pd );
					}

					Player player = SafeSpawn.instance().getServer().getPlayer( pd.getName() );
					if ( player == null || !player.isOnline() ) {
						Log.line( "Removing: %s", pd.getName() );
						SafeSpawn.instance().getPlayerStore().removeFromCache( pd.getName() );
					}
				}
			}
		} );
	}

	/**
	 * R can only be positive or zero and preferably rather small say like no more than 2 or 3?
	 * 
	 * @param r refers to the number of blocks outward from the center block to search, r of 1 will give diameter of 3.
	 * @return
	 */
	private static Set<String> findActiveChunkHashes( int r ) {
		try {
			// find all active chunks
			Set<String> activeChunks = Sets.newHashSet();
			for ( World w : SafeSpawn.instance().getServer().getWorlds() ) {
				List<Player> players = Lists.newArrayList( SafeSpawn.instance().getServer().getWorld( w.getUID() ).getPlayers() );
				for ( Player p : players ) {
					Chunk pChunk = p.getLocation().getChunk();
					int cx = pChunk.getX();
					int cz = pChunk.getZ();
					for ( int x = ( cx - r ); x <= ( cx + r ); x++ ) {
						for ( int z = ( cz - r ); z <= ( cz + r ); z++ ) {
							activeChunks.add( BlockStore.toChunkHash( w.getName(), x, z ) );
						}
					}
				}
			}
			return activeChunks;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	public static void onPlayerPortalEvent( SafeSpawn plugin, PlayerMoveEvent event ) {
		onPlayerMoveEvent( plugin, event );
	}

	public static void onPlayerTeleportEvent( SafeSpawn plugin, PlayerMoveEvent event ) {
		onPlayerMoveEvent( plugin, event );
	}

	public static void onPlayerMoveEvent( SafeSpawn plugin, PlayerMoveEvent event ) {
		try {
			Location from = event.getFrom();
			Location to = event.getTo();
			if ( from.getChunk() != to.getChunk() ) { // chunk change
				onPlayerEnteredNewChunk( plugin, to.getChunk() );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onPlayerQuitEvent( SafeSpawn plugin, PlayerQuitEvent event ) {
		try {
			// Location from = event.getPlayer().getLocation();
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onPlayerJoinEvent( SafeSpawn plugin, PlayerJoinEvent event ) {
		try {
			Location to = event.getPlayer().getLocation();
			onPlayerEnteredNewChunk( plugin, to.getChunk() );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	private static void onPlayerEnteredNewChunk( SafeSpawn plugin, Chunk chunk ) {
		try {
			Log.line( "Player entered: %s %sx%s", chunk.getWorld().getName(), chunk.getX(), chunk.getZ() );
			plugin.getTaskman().addNonRepeatingTask( new ChunkBlocksLoader( chunk ) );
			// plugin.getTaskman().addNonRepeatingTask( new ChunkBlocksGC( chunk ) ); // done serially from ChunkBlocksLoader instead
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}
