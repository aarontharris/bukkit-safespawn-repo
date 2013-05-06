package com.ath.bukkit.safespawn;

import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ath.bukkit.safespawn.data.BlockData;
import com.ath.bukkit.safespawn.data.Blocks;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class Functions {

	public static Joiner spaceJoiner = Joiner.on( " " );

	public static String joinSpace( String... strings ) {
		return spaceJoiner.join( strings );
	}

	public static String joinSpace( Collection<String> strings ) {
		return spaceJoiner.join( strings );
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
					Log.line( " contains " + mat );
					if ( !inv.contains( mat, count ) ) {
						Log.line( " contains " + mat + " false" );
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

	/** assumes the block IS material.chest */
	public static Chest blockToChest( Block block ) {
		Chest chest = (Chest) block.getState();
		return chest;
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

	public static Block getTargetBlock( Player player, int maxDist, Material mat ) {
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

	/**
	 * magic signs can lock a relative position<br>
	 * the magic sign itself and the block at the relative position are owned and protected<br>
	 * <br>
	 * lock
	 * 0
	 * -1
	 * 0
	 */
	public static boolean isOwnedBlock( Location l, Material m ) {
		try {
			if ( l != null && m != null ) {
				if ( Material.WALL_SIGN.equals( m ) ) {
					BlockData bd = BlockData.get( l, m );
					if ( bd != null ) {
						if ( Blocks.isMagical( bd ) ) {
							if ( Blocks.getWriteAccess( bd ).size() > 0 ) {
								return true;
							}
						}
					}
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	public static boolean canUserAccessBlock( Location l, Material m, Player p ) {
		try {
			if ( isOwnedBlock( l, m ) ) {
				BlockData bd = BlockData.get( l, m );
				if ( Blocks.canRead( bd, p ) ) {
					return true;
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	/**
	 * @deprecated
	 * @param l
	 * @param m
	 * @return
	 */
	public static boolean isUserOwnerOfBlock( Location l, Material m ) {
		try {
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}
}
