package com.ath.bukkit.safespawn.magic.sign;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicCommand;

public class ShrineSign extends MagicSign {

	@Override
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		try {
			Location l = sign.getBlock().getLocation();

			int x = Location.locToBlock( l.getX() );
			int y = Location.locToBlock( l.getY() );
			int z = Location.locToBlock( l.getZ() );

			// // move 1 unit in the direction the sign is facing
			// switch ( event.getBlockFace() ) {
			// case NORTH:
			// z -= 1;
			// break;
			// case EAST:
			// x += 1;
			// break;
			// case SOUTH:
			// z += 1;
			// break;
			// case WEST:
			// x -= 1;
			// break;
			// }

			// move down 1 unit
			y -= 1;

			Block block = event.getPlayer().getWorld().getBlockAt( x, y, z );
			SafeSpawn.logLine( String.format( "%s @ %s, %s, %s", block.getType().toString(), x, y, z ) );

			// FIXME chest
			if ( block.getType().equals( Material.CHEST ) ) {
				Chest chest = (Chest) block.getState();
				Inventory inv = chest.getInventory();

				if ( inv.contains( Material.STICK, 2 ) ) {
					if ( inv.contains( Material.STONE, 2 ) ) {
						if ( inv.contains( Material.BONE, 2 ) ) {
							event.getPlayer().sendMessage( MagicCommand.Manifest.getWord() );
							return true;
						}
					}
				}
			}

		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
		return false;
	}
}
