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
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.magic.MagicWords;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicCommand;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicWord;

public class ShrineSign extends MagicSign {

	@Override
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		try {
			Log.line( "shrine sign" );
			Location l = sign.getBlock().getLocation();

			MagicWord word = MagicWords.readMagicWordFromLine( sign.getLine( 1 ) );

			// Shrine that does not require an offering -- no chest required
			{
				// SYMPATH
				if ( MagicWord.SYMPATHY.equals( word ) ) {
					boolean isEmpty = true;
					for ( ItemStack stack : event.getPlayer().getInventory() ) {
						if ( stack != null && stack.getAmount() > 0 ) {
							isEmpty = false;
							break;
						}
					}

					if ( isEmpty ) {
						Log.line( "SYMPATH - empty" );
						event.getPlayer().getInventory().addItem( new ItemStack( Material.STONE_SWORD ) );
						event.getPlayer().getInventory().addItem( new ItemStack( Material.STONE_PICKAXE ) );
						event.getPlayer().getInventory().addItem( new ItemStack( Material.STONE_AXE ) );
						event.getPlayer().getInventory().addItem( new ItemStack( Material.STONE_SPADE ) );
						event.getPlayer().getInventory().addItem( new ItemStack( Material.APPLE, 10 ) );
						String message = Functions.randomMessage(
								"Thank you come again!",
								"Check your inventory...",
								Functions.capitalize( MagicWord.SYMPATHY.getWord() ) + "!"
								);
						event.getPlayer().sendMessage( message );
						return true;
					} else {
						String message = Functions.randomMessage(
								"Hmmm... Perhaps if you were poorer...",
								"You don't look needy.",
								"...",
								"None for the greedy!",
								"The rich do not get richer here.",
								"I see so much in your inventory",
								"Wait while I tell you your future... NOTHING!  ABSOLUTELY NOTHING! HAHAHA!"
								);
						event.getPlayer().sendMessage( message );
						return false;
					}
				}
			}

			int x = Location.locToBlock( l.getX() );
			int y = Location.locToBlock( l.getY() );
			int z = Location.locToBlock( l.getZ() );

			// move down 1 unit
			y -= 1;

			Block block = event.getPlayer().getWorld().getBlockAt( x, y, z );

			Log.line( " block is " + block.getType() );

			if ( block.getType().equals( Material.CHEST ) ) {
				Log.line( " is chest " );
				Chest chest = Functions.blockToChest( block );
				Inventory inv = chest.getInventory();



				if ( MagicWord.EGG_RANDOM.equals( word ) ) {
					return true;
				}

				// For now we'll leave this but lets make this an action like those above
				if ( Functions.removeFromInventory( inv, 2, Material.STICK, Material.STONE, Material.BONE ) ) {
					event.getPlayer().sendMessage( MagicCommand.Manifest.getWord() );
					ItemStack stack = EggHatchery.newEgg( EggStackType.Zombie );
					inv.addItem( stack );
					return true;
				}
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}
}
