package com.ath.bukkit.safespawn.magic.sign;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ath.bukkit.safespawn.Const;
import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.magic.MagicWords;
import com.ath.bukkit.safespawn.magic.MagicWords.MagicWord;


// teles:352:oh  // command : itemInHand : world
//        31     //            hex of x
//        41     //            hex of y
//       14D     //            hex of z
//
// itemInHand is the materialId, but can be "non" for empty or in this case, its ignored
public class TeleporterSign extends MagicSign {

	private MagicWord itemInHand;
	private MagicWord requiredInHand;
	private String worldName;
	private int x;
	private int y;
	private int z;

	@Override
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		try {
			ItemStack itemStack = event.getPlayer().getItemInHand();
			if ( itemStack != null ) {
				itemInHand = MagicWord.findItemByMaterial( itemStack.getType() );

				// line 1
				String line1 = sign.getLine( 0 );
				String l1Parts[] = line1.split( Const.MW_separator );
				requiredInHand = MagicWord.findItemByWord( l1Parts[1] );

				if ( !requiredInHand.matches( itemInHand ) ) {
					return false;
				}

				String worldWord = l1Parts[2];
				worldName = SafeSpawn.instance().getWorldsManager().findByMagic( worldWord );
				if ( worldName == null )
					return false;
				World world = SafeSpawn.instance().getServer().getWorld( worldName );
				if ( world == null )
					return false;

				// line 2
				String line2 = sign.getLine( 1 );
				x = MagicWords.readCoordinateFromLine( line2 );

				String line3 = sign.getLine( 2 );
				y = MagicWords.readCoordinateFromLine( line3 );

				String line4 = sign.getLine( 3 );
				z = MagicWords.readCoordinateFromLine( line4 );

				if ( world != null ) {
					Location location = new Location( world, x, y, z );
					F.teleport( SafeSpawn.instance(), event.getPlayer(), location );
					return true;
				}
			}
		} catch ( Exception e ) {
			Log.error( e ); // TODO Mute me
		}
		return false;
	}

}
