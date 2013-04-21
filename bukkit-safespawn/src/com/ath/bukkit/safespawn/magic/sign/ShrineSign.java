package com.ath.bukkit.safespawn.magic.sign;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ath.bukkit.safespawn.EggHatchery;
import com.ath.bukkit.safespawn.EggHatchery.EggStackType;
import com.ath.bukkit.safespawn.Functions;
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

			// move down 1 unit
			y -= 1;

			Block block = event.getPlayer().getWorld().getBlockAt( x, y, z );

			if ( block.getType().equals( Material.CHEST ) ) {
				Chest chest = Functions.blockToChest( block );
				Inventory inv = chest.getInventory();

				if ( Functions.removeFromInventory( inv, 2, Material.STICK, Material.STONE, Material.BONE ) ) {
					event.getPlayer().sendMessage( MagicCommand.Manifest.getWord() );

					// MagicWord word = MagicWords.readMagicWordFromLine( sign.getLine( 1 ), Const.MW_separator, 0 );
					// if ( word.matches( MagicWord.EGG_ZOMBIE ) { }

					ItemStack stack = EggHatchery.newEgg( EggStackType.Zombie );
					inv.addItem( stack );

					return true;
				}
			}
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
		return false;
	}
}
