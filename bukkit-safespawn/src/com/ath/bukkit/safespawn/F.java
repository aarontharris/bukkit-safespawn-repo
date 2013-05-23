package com.ath.bukkit.safespawn;

import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.ath.bukkit.safespawn.data.BlockData;
import com.ath.bukkit.safespawn.data.Blocks;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class F {

	private static final HashSet<Byte> AIR_WATER_FILTER = Sets.newHashSet(
			(byte) Material.AIR.getId(),
			(byte) Material.WATER.getId()
			);

	private static final HashSet<Byte> AIR_WATER_SIGN_FILTER = Sets.newHashSet(
			(byte) Material.AIR.getId(),
			(byte) Material.WATER.getId(),
			(byte) Material.WALL_SIGN.getId()
			);

	public static Joiner spaceJoiner = Joiner.on( " " );

	public static String joinSpace( String... strings ) {
		return spaceJoiner.join( strings );
	}

	public static String joinSpace( Collection<String> strings ) {
		return spaceJoiner.join( strings );
	}

	/** empty or null */
	public static boolean isEmpty( String string ) {
		if ( string == null || string.isEmpty() ) {
			return true;
		}
		return false;
	}

	public static boolean insideZone( Zone z, Location l ) {
		if ( z != null && z.getLocation() != null && l != null ) { // data is good
			if ( z.getLocation().getWorld().equals( l.getWorld() ) ) { // worlds match
				return insideRadius( z.getLocation().getX(), z.getLocation().getY(), z.getLocation().getZ(), z.getRadius(), l.getX(), l.getY(), l.getZ() );
			}
		}
		return false;
	}

	/**
	 * Is xyz2 inside xyz1 & radius<br>
	 * Warning: very very large radius can be bad, something like 100 or even 1000 is OK.
	 */
	public static boolean insideRadius( double x1, double y1, double z1, int radius, double x2, double y2, double z2 ) {
		// first do a quick bounding box check to avoid doing sqrts on enormous distances that aren't applicable
		if ( x2 > ( x1 + radius ) || x2 < ( x1 - radius ) ) {
			return false;
		}
		if ( y2 > ( y1 + radius ) || y2 < ( y1 - radius ) ) {
			return false;
		}
		if ( z2 > ( z1 + radius ) || z2 < ( z1 - radius ) ) {
			return false;
		}

		// now do a mathier check, very large distances can be problematic
		// however we'll never get here unless the radius is very large
		double deltaX = x2 - x1;
		double deltaY = y2 - y1;
		double deltaZ = z2 - z1;
		double distance = Math.sqrt( deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ );
		if ( distance < radius ) {
			return true;
		}
		return false;
	}

	public static void teleport( SafeSpawn plugin, Player player, World world, double x, double y, double z ) {
		teleport( plugin, player, new Location( world, x, y, z ) );
	}

	public static void teleport( SafeSpawn plugin, Player player, Location loc ) {
		try {
			if ( player != null ) {
				Location teleport = loc.clone();
				teleport.setPitch( player.getLocation().getPitch() );
				teleport.setYaw( player.getLocation().getYaw() );
				player.teleport( teleport );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	@SuppressWarnings( "incomplete-switch" )
	public static boolean canCarryPlague( EntityType type ) {
		switch ( type ) {
		case PLAYER:
			return true;
		case ZOMBIE:
			return true;
		case SKELETON:
			return true;
		case SPIDER:
			return true;
		case CAVE_SPIDER:
			return true;
		case SLIME:
			return true;
		}
		return false;
	}

	@SuppressWarnings( "incomplete-switch" )
	public static boolean canCarryVampirism( EntityType type ) {
		switch ( type ) {
		case PLAYER:
			return true;
		case PIG_ZOMBIE:
			return true;
		}
		return false;
	}

	public static boolean isBaddie( EntityType type ) {
		switch ( type ) {
		case BLAZE:
			return true;
		case CAVE_SPIDER:
			return true;
		case CREEPER:
			return true;
		case ENDERMAN:
			return true;
		case ENDER_DRAGON:
			return true;
		case GHAST:
			return true;
		case GIANT:
			return true;
		case IRON_GOLEM:
			return true;
		case MAGMA_CUBE:
			return true;
		case PIG_ZOMBIE:
			return true;
		case SILVERFISH:
			return true;
		case SKELETON:
			return true;
		case SLIME:
			return true;
		case SPIDER:
			return true;
		case WITCH:
			return true;
		case ZOMBIE:
			return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * Remove {count} items of each {materials} from {inv} and return true if it was successful.<br>
	 * 
	 * @param inv
	 * @param count
	 * @param materials
	 * @return false if the {inv} did not contain at least {count} of each {materials}
	 */
	public static boolean removeFromInventory( Inventory inv, int count, Material... materials ) {
		try {
			if ( inv != null && materials != null ) {
				// first do a quick check to see if the mats need are in the inventory before doing all the other work...
				for ( Material mat : materials ) {
					if ( !inv.contains( mat, count ) ) {
						return false;
					}
				}

				for ( Material mat : materials ) {
					HashMap<Integer, ? extends ItemStack> all = inv.all( mat );
					int remaining = count;
					for ( ItemStack stack : all.values() ) {
						if ( stack.getAmount() == remaining ) {
							inv.remove( stack );
							break;
						}
						if ( stack.getAmount() > remaining ) {
							stack.setAmount( stack.getAmount() - remaining );
							break;
						}
						if ( stack.getAmount() < remaining ) {
							remaining -= stack.getAmount();
							inv.remove( stack );
						}
						if ( remaining <= 0 ) {
							break;
						}
					}
				}

				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}


	@SuppressWarnings( "incomplete-switch" )
	public static void adjustXZ( Location l, double offset, BlockFace blockFace ) {
		switch ( blockFace ) {
		case NORTH:
			l.setZ( l.getZ() - offset );
			break;
		case SOUTH:
			l.setZ( l.getZ() + offset );
			break;
		case EAST:
			l.setX( l.getX() + offset );
			break;
		case WEST:
			l.setX( l.getX() - offset );
			break;
		}
	}

	/** return null if not a sign or not found */
	@SuppressWarnings( "incomplete-switch" )
	public static Block getBlockWallSignIsAttachedTo( Location l, Material m ) {
		try {
			if ( l != null && m != null ) {
				Block block = l.getWorld().getBlockAt( l );
				BlockFace facing = getFacing( block );
				if ( facing != null ) {
					int x = l.getBlockX();
					int y = l.getBlockY();
					int z = l.getBlockZ();

					switch ( facing.getOppositeFace() ) {
					case NORTH:
						z -= 1;
						break;
					case SOUTH:
						z += 1;
						break;
					case EAST:
						x += 1;
						break;
					case WEST:
						x -= 1;
						break;
					}

					Block attachedTo = l.getWorld().getBlockAt( x, y, z );
					if ( attachedTo != null ) {
						return attachedTo;
					}
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** assumes the block IS material.chest */
	public static Chest blockToChest( Block block ) {
		Chest chest = (Chest) block.getState();
		return chest;
	}

	/** return null if block is not a sign */
	public static org.bukkit.block.Sign blockToBlockSign( Block block ) {
		try {
			if ( block != null ) {
				BlockState state = block.getState();
				if ( state instanceof org.bukkit.block.Sign ) {
					return (org.bukkit.block.Sign) state;
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** return null if block is not a sign */
	public static org.bukkit.material.Sign blockToMatSign( Block block ) {
		try {
			if ( block != null ) {
				BlockState state = block.getState();
				MaterialData data = state.getData();
				if ( data != null && data instanceof org.bukkit.material.Sign ) {
					return (org.bukkit.material.Sign) data;
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** returns null if has no facing aka not a sign */
	public static BlockFace getFacing( Block block ) {
		try {
			org.bukkit.material.Sign sign = blockToMatSign( block );
			if ( sign != null ) {
				return sign.getFacing();
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	public static String toString( Block b ) {
		try {
			if ( b != null ) {
				return toString( b.getLocation() ) + ",mat=" + b.getType();
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return "null";
	}

	public static String toString( Location l ) {
		try {
			if ( l != null ) {
				return "x=" + (int) l.getX() + ",y=" + (int) l.getY() + ",z=" + (int) l.getZ() + ",w=" + l.getWorld().getName();
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return "null";
	}

	public static String toString( Chunk c ) {
		try {
			if ( c != null ) {
				return "w=" + c.getWorld().getName() + ",x=" + c.getX() + ",z=" + c.getZ();
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return "null";
	}

	public static String randomMessage( String... messages ) {
		try {
			if ( messages != null && messages.length > 0 ) {
				if ( messages.length == 1 ) {
					return messages[0];
				}

				Random r = new Random( System.currentTimeMillis() );
				return messages[r.nextInt( messages.length )];
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return "...";
	}

	public static String capitalize( String word ) {
		return capitalize( CharBuffer.wrap( word.toCharArray() ) ).toString();
	}

	public static CharBuffer capitalize( CharBuffer buffer ) {
		char c = buffer.get( 0 );
		buffer.put( 0, Character.toUpperCase( c ) );
		return buffer;
	}

	public static void debugBlock( Block block ) {
		if ( block == null ) {
			Log.line( "blocks is null" );
		} else {
			Log.line( "block: %s is %s", BlockData.toHash( block ), block.getType() );
		}
	}

	public static void debugBlocks( List<Block> blocks ) {
		if ( blocks == null ) {
			Log.line( "blocks is null" );
		} else if ( blocks.isEmpty() ) {
			Log.line( "blocks is empty" );
		} else {
			for ( int i = 0; i < blocks.size(); i++ ) {
				Log.line( "blocks[%s]: %s is %s", i, BlockData.toHash( blocks.get( i ) ), blocks.get( i ).getType() );
			}
		}
	}

	public static String fromDbSafeString( String string ) {
		try {
			if ( string != null ) {
				return string.replaceAll( "''", "'" );
				// return string.replaceAll( "\\\\([^\\\\])", "$1" );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** returns null on error */
	public static String toDbSafeString( String string ) {
		try {
			if ( string != null ) {
				// return string.replaceAll( "([^a-zA-Z0-9_,:\\s\\{\\}\\\"\\[\\]\\(\\)])", "\\\\$1" );
				return string.replaceAll( "'", "''" );
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/**
	 * ignores the block at location 'nearThis'<br>
	 * cubic dist check<br>
	 * dist must be >= 0<br>
	 * careful, bigger maxDist gets O^3 more expensive -- try to keep it small<br>
	 * 
	 * @return never null;
	 */
	public static Set<Block> findBlock( Location nearThis, Material likeThis, int maxDist, boolean ignoreOrigin ) {
		return findBlock( nearThis, likeThis, maxDist, maxDist, maxDist, ignoreOrigin );
	}

	/**
	 * cubic dist check<br>
	 * dist must be >= 0<br>
	 * careful, bigger maxDist gets O^3 more expensive -- try to keep it small<br>
	 * 
	 * @return never null;
	 */
	public static Set<Block> findBlock( Location nearThis, Material likeThis, int maxDistX, int maxDistY, int maxDistZ, boolean ignoreOrigin ) {
		try {
			if ( nearThis != null && likeThis != null ) {
				int ox = nearThis.getBlockX();
				int oy = nearThis.getBlockY();
				int oz = nearThis.getBlockZ();

				int sx = ox - maxDistX;
				int sy = oy - maxDistY;
				int sz = oz - maxDistZ;

				int ex = sx + ( 2 * maxDistX );
				int ey = sy + ( 2 * maxDistY );
				int ez = sz + ( 2 * maxDistZ );

				Set<Block> out = Sets.newHashSet();
				World w = nearThis.getWorld();
				for ( int x = sx; x <= ex; x++ ) {
					for ( int y = sy; y <= ey; y++ ) {
						for ( int z = sz; z <= ez; z++ ) {
							Block block = w.getBlockAt( x, y, z );
							if ( block.getTypeId() == likeThis.getId() ) {
								if ( !ignoreOrigin || ( x != ox || y != oy || z != oz ) ) {
									out.add( block );
								}
							}
						}
					}
				}
				return out;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return Collections.emptySet();
	}

	/**
	 * not ordered if more than one matching is found but does search left to right
	 */
	public static Block findFirstMatchingAdjacentBlock( Location nearThis, Material likeThis, boolean ignoreOrigin ) {
		Set<Block> blocks = findBlock( nearThis, likeThis, 1, ignoreOrigin );
		for ( Block b : blocks ) {
			return b;
		}
		return null;
	}

	/** @return the block (sign) or null if not magical and owned */
	public static Block isOwnedWallSign( Location l, Material m ) {
		try {
			if ( Material.WALL_SIGN.equals( m ) ) {
				BlockData bd = BlockData.get( l, m );
				if ( bd != null ) {
					if ( Blocks.isMagical( bd ) ) {
						if ( Blocks.hasOwner( bd ) ) {
							return l.getBlock();
						}
					}
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** @return the controlling block (sign) or null */
	public static Block isBelowOwnedWallSign( Location l, Material m ) {
		try {
			Block blockAbove = l.getWorld().getBlockAt( l.getBlockX(), l.getBlockY() + 1, l.getBlockZ() );
			if ( null != isOwnedWallSign( blockAbove.getLocation(), blockAbove.getType() ) ) {
				return blockAbove;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** @return the controlling block (sign) or null */
	public static Block isAdjacentToOwnedWallSign( Location l, Material m ) {
		try {
			int idx = 0;
			while ( AdjBlockIterator.hasNext( idx ) ) {
				Block adj = AdjBlockIterator.next( l, idx );
				if ( null != isOwnedWallSign( adj.getLocation(), adj.getType() ) ) {
					return adj;
				}
				idx++ ;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	public static void getAdjacentBlocks( Location l, Material m, Collection<Block> out ) {
		try {
			Log.line( " - geAdjacentBlocks" );
			int idx = 0;
			while ( AdjBlockIterator.hasNext( idx ) ) {
				Log.line( " - geAdjacentBlocks %s", idx );
				Block adj = AdjBlockIterator.next( l, idx );
				Log.line( " - geAdjacentBlocks %s = %s", idx, F.toString( adj ) );
				out.add( adj );
				idx++ ;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	/**
	 * magic signs can lock a relative position<br>
	 * the magic sign itself, the block the sign is attached to, and the block below are protected<br>
	 * <br>
	 * lock
	 * 0
	 * -1
	 * 0
	 * 
	 * @return the block (sign) controlling this owned block or null
	 */
	public static Block isOwnedBlock( Location l, Material m ) {
		try {
			if ( l != null && m != null ) {
				Block ctrl = isOwnedWallSign( l, m );
				if ( ctrl != null ) {
					return ctrl;
				}

				ctrl = isAdjacentToOwnedWallSign( l, m );
				if ( ctrl != null ) {
					return ctrl;
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}

	/** true if the block is just a regular block or if it is an owned block and the player has access */
	public static boolean canUserAccessBlock( Location l, Material m, Player p ) {
		try {
			BlockData bd = BlockData.get( l, m );
			if ( bd == null ) {
				return true;
			}
			if ( Blocks.canAccess( bd, p ) ) {
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/**
	 * @deprecated not implemented yet
	 * @param l
	 * @param m
	 * @return
	 */
	public static boolean isUserOwnerOfBlock( Location l, Material m ) {
		try {
			// FIXME: isUserOwnerOfBlock
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static boolean isMagicAllowed( Location l, Material m ) {
		try {
			if ( Material.WALL_SIGN.equals( m ) ) {
				Block attachedTo = getBlockWallSignIsAttachedTo( l, m );

				Material am = attachedTo.getType();
				if ( am.hasGravity() ) {
					return false;
				}

				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static int SEC_SUNDOWN = 13050;
	public static int SEC_SUNRISE = 22950;

	public static boolean isSunUp( World w ) {
		try {
			long time = w.getTime();
			if ( time >= 24000 )
				time = time % 24000;

			boolean night = ( time > SEC_SUNDOWN && time < SEC_SUNRISE );
			if ( !night ) {
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/**
	 * Because line of sight or targetBlock, or lastTwoTarget etc, will
	 * collide with the transparent space of another wallsign beside/below/above it.
	 * This ensures you get the block you have highlighted
	 * 
	 * @param player
	 * @return
	 */
	public static Block getTargetWallSign( Player player ) {
		List<Block> blocks = player.getLineOfSight( AIR_WATER_SIGN_FILTER, 5 );
		if ( blocks == null || blocks.isEmpty() ) {
			return null;
		}

		Block block = null;

		for ( int i = 0; i < blocks.size(); i++ ) {
			Block b = blocks.get( i );
			if ( Material.WALL_SIGN.equals( b.getType() ) ) {
				block = b;
			} else if ( !AIR_WATER_SIGN_FILTER.contains( (byte) b.getTypeId() ) ) {
				break;
			}
		}

		return block;
	}

	/**
	 * Because line of sight or targetBlock, or lastTwoTarget etc, will
	 * collide with the transparent space of another wallsign beside/below/above it.
	 * This ensures you get the block you have highlighted
	 * 
	 * @param player
	 * @return
	 */
	public static Block getTargetBlockClose( Player player, int maxDist, Material mat ) {
		HashSet<Byte> filter = new HashSet<Byte>( AIR_WATER_FILTER );
		filter.add( (byte) mat.getId() );
		List<Block> blocks = player.getLineOfSight( filter, 5 );
		if ( blocks == null || blocks.isEmpty() ) {
			return null;
		}

		Block block = null;

		for ( int i = 0; i < blocks.size(); i++ ) {
			Block b = blocks.get( i );
			if ( mat.equals( b.getType() ) ) {
				block = b;
			} else if ( !AIR_WATER_FILTER.contains( (byte) b.getTypeId() ) ) {
				break;
			}
		}

		return block;
	}

	/** Don't use this unless you're far away, like +10 units, otherwise it has some funk, see notes on getTargetWallSign */
	public static Block getTargetBlockFarAway( Player player, int maxDist, Material mat ) {
		try {
			List<Block> blocks = player.getLastTwoTargetBlocks( null, 5 );

			Block block = null;
			for ( Block b : blocks ) {
				if ( b.getType().equals( mat ) ) {
					block = b;
					break;
				}
			}

			return block;
		} catch ( Exception e ) {
			Log.error( e );
		}
		return null;
	}
}
