package com.ath.bukkit.safespawn;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Random;

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

public class Functions {

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
			SafeSpawn.logError( e );
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
					SafeSpawn.logLine( " contains " + mat );
					if ( !inv.contains( mat, count ) ) {
						SafeSpawn.logLine( " contains " + mat + " false" );
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
			SafeSpawn.logError( e );
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

	public static String toString( Location l ) {
		try {
			if ( l != null ) {
				return "x=" + (int) l.getX() + ",y=" + (int) l.getY() + ",z=" + (int) l.getZ() + ",w=" + l.getWorld().getName();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
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
			SafeSpawn.logError( e );
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

	public static void main( String args[] ) {
		String word = "blah";
		System.out.println( capitalize( word ) );
	}
}
