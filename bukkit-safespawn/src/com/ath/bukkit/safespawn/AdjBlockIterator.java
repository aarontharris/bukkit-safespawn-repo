package com.ath.bukkit.safespawn;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Made this static so we dont have to instantiate a new iterator for every check.
 * 
 * <pre>
 * <code>
 * Block centerBlock = ...;
 * int idx = 0;
 * while( AdjBlockIterator.hasNext( idx ) ) {
 *   Block adjacentBlock = AdjBlockIterator.next( centerBlock.loc, idx );
 *   ...
 *   idx++;  // very important not to leave this part out!
 * }
 * </code>
 * </pre>
 */
public final class AdjBlockIterator {
	public static final int numSides = 6;
	public static final int[][] itMatrix = new int[][] {
			{ -1, 0, 0 },
			{ 0, 0, -1 },
			{ 1, 0, 0 },
			{ 0, 0, 1 },
			{ 0, -1, 0 },
			{ 0, 1, 0 }
	};

	/** idx must be 0-5, totalling 6 adjacent checks */
	public final static boolean hasNext( int idx ) {
		return ( idx + 1 ) <= numSides;
	}

	/** idx must be 0-5, totalling 6 adjacent checks */
	public final static Block next( Location loc, int idx ) {
		return loc.getWorld().getBlockAt(
				loc.getBlockX() + itMatrix[idx][0],
				loc.getBlockY() + itMatrix[idx][1],
				loc.getBlockZ() + itMatrix[idx][2]
				);
	}
}
