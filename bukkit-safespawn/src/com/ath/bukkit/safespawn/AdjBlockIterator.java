package com.ath.bukkit.safespawn;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Made this static so we dont have to instantiate a new iterator for every check.<br>
 * includes block above, below, toLeft, toRight, inFront, inBack<br>
 * does not include diagonally adjacent blocks<br>
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
	public static final boolean hasNext( int idx ) {
		return ( idx + 1 ) <= numSides;
	}

	/** idx must be 0-5, totalling 6 adjacent checks */
	public static final Block next( Location loc, int idx ) {
		return loc.getWorld().getBlockAt(
				loc.getBlockX() + itMatrix[idx][0],
				loc.getBlockY() + itMatrix[idx][1],
				loc.getBlockZ() + itMatrix[idx][2]
				);
	}

	// /** idx must be 0-5, totalling 6 adjacent checks */
	// public static final BlockFace getFace( BlockFace relTo, int idx ) {
	// switch ( idx ) {
	// case 0:
	// return
	// case 1:
	// case 2:
	// case 3:
	// case 4:
	// case 5:
	// }
	// throw new IllegalStateException( "There is no face for idx " + idx );
	// }
}
