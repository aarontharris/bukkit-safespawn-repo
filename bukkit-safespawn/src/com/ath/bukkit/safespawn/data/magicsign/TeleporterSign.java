package com.ath.bukkit.safespawn.data.magicsign;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ath.bukkit.safespawn.Functions;
import com.ath.bukkit.safespawn.SafeSpawnPlugin;


// teles:352:oh
// 31
// 41
// 14D
public class TeleporterSign extends MagicSign {

	private int materialInHandId;
	private int requiredMaterialId;
	private String worldName;
	private int x;
	private int y;
	private int z;

	@Override
	public boolean evokeSign( Sign sign, PlayerInteractEvent event ) {
		try {
			ItemStack itemStack = event.getPlayer().getItemInHand();
			if ( itemStack != null ) {
				materialInHandId = itemStack.getType().getId();

				// line 1
				String line1 = sign.getLine( 0 );
				String l1Parts[] = line1.split( ":" );
				// skip part[0], its the type and we already know it
				requiredMaterialId = Integer.parseInt( l1Parts[1] );
				if ( materialInHandId != requiredMaterialId )
					return false;
				String worldWord = l1Parts[2];
				worldName = SafeSpawnPlugin.instance().getWorldsManager().findByMagic( worldWord );
				if ( worldName == null )
					return false;
				World world = SafeSpawnPlugin.instance().getServer().getWorld( worldName );
				if ( world == null )
					return false;

				// line 2
				String line2 = sign.getLine( 1 );
				x = decodeCoordinate( line2 );

				String line3 = sign.getLine( 2 );
				y = decodeCoordinate( line3 );

				String line4 = sign.getLine( 3 );
				z = decodeCoordinate( line4 );

				if ( world != null ) {
					Location location = new Location( world, x, y, z );
					Functions.teleport( SafeSpawnPlugin.instance(), event.getPlayer(), location );
					return true;
				}
			}
		} catch ( Exception e ) {
			SafeSpawnPlugin.logError( e ); // TODO Mute me
		}
		return false;
	}

	private int decodeCoordinate( String line ) {
		if ( line.startsWith( "-" ) ) {
			return -Integer.decode( "0x" + line.substring( 1 ) );
		} else {
			return Integer.decode( "0x" + line );
		}
	}
}
