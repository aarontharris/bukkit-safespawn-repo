package com.ath.bukkit.safespawn;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
					if ( !inv.contains( mat, count ) ) {
						return false;
					}
				}

				for ( Material mat : materials ) {
					SafeSpawn.logLine( "Removing " + mat );
					HashMap<Integer, ? extends ItemStack> all = inv.all( mat );
					int remaining = count;
					for ( ItemStack stack : all.values() ) {
						SafeSpawn.logLine( "Removing from " + mat + " with " + stack.getAmount() );
						if ( stack.getAmount() == remaining ) {
							SafeSpawn.logLine( " -- Removing the whole stack" );
							inv.remove( stack );
							break;
						}
						if ( stack.getAmount() > remaining ) {
							SafeSpawn.logLine( " -- Removing " + remaining + " from the stack and satisfied" );
							stack.setAmount( stack.getAmount() - remaining );
							break;
						}
						if ( stack.getAmount() < remaining ) {
							remaining -= stack.getAmount();
							SafeSpawn.logLine( " -- Removing the whole stack and " + remaining + " remaining" );
							inv.remove( stack );
						}
						if ( remaining <= 0 ) {
							SafeSpawn.logLine( " -- Satisfied -- should never see this" );
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
}
