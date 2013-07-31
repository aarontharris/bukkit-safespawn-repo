package com.ath.bukkit.safespawn.magic.sign;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ath.bukkit.safespawn.F;
import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.BlockData;
import com.ath.bukkit.safespawn.data.Blocks;


// teles:352:oh  // command : itemInHand : world
//        31     //            hex of x
//        41     //            hex of y
//       14D     //            hex of z
//
// itemInHand is the materialId, but can be "non" for empty or in this case, its ignored
public class TeleporterSign extends MagicSign {

	@Override
	public boolean enhance( Sign sign, Player player ) {
		try {
			String bindingWord = sign.getLine( 1 ); // 1 == line #2
			BlockData bd = BlockData.get( sign.getLocation(), sign.getType() );
			if ( !F.isEmpty( bindingWord ) && bd != null ) {
				bd.updateRef( toTeleporterRef( bindingWord, player.getName() ) );
				return true;
			}
		} catch ( Exception e ) {
			Log.error( e );
		}
		return false;
	}

	// private MagicWord itemInHand;
	// private MagicWord requiredInHand;
	// private String worldName;
	// private int x;
	// private int y;
	// private int z;

	@Override
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		try {
			Player player = event.getPlayer();

			String bindingWord = sign.getLine( 1 ); // 1 == line #2
			if ( F.isEmpty( bindingWord ) ) {
				Log.line( "bindingWord is empty" );
				player.sendMessage( "This sign has not been paired" );
				player.sendMessage( "A teleporter sign must be paired with another sign." );
				return false;
			}

			BlockData bd = BlockData.get( sign.getLocation(), sign.getType() );

			if ( F.isEmpty( bd.getRef() ) ) {
				Log.line( "bd.ref is empty" );
				player.sendMessage( "This sign has not been paired" );
				player.sendMessage( "A teleporter sign must be paired with another sign." );
				return false;
			}

			// find partner as <ownername>_<bindingWord>
			BlockData partner = null;
			Set<BlockData> matches = SafeSpawn.instance().getBlockStore().dbFindByRef( toTeleporterRef( bindingWord, bd.getOwner() ) );
			if ( matches != null && matches.size() > 0 ) {
				for ( BlockData match : matches ) {
					if ( match == null || match.getHash().equals( bd.getHash() ) ) {
						continue; // skip the local sign, we're looking for the remote sign
					}
					if ( partner == null || match.getLastModified() > partner.getLastModified() ) {
						partner = match; // may be more than one, if so only use the most recently updated... :/
					}
				}
			}

			if ( partner == null ) {
				player.sendMessage( "Something is wrong with the other end of the portal" );
				player.sendMessage( "A teleporter sign must be paired with another sign." );
				return false;
			}

			if ( !Blocks.isMagical( partner ) ) {
				player.sendMessage( "Something is wrong with the other end of the portal" );
				player.sendMessage( "A teleporter sign must be paired with another sign." );
				return false;
			}

			// get remote sign teleport location
			Location location = null;
			World w = SafeSpawn.instance().getServer().getWorld( partner.getBlockW() );
			if ( w != null ) {
				Block partnerBlock = w.getBlockAt( partner.getBlockX(), partner.getBlockY(), partner.getBlockZ() );
				BlockFace facing = F.getFacing( partnerBlock );
				location = new Location( w, partner.getBlockX(), partner.getBlockY(), partner.getBlockZ() );
				F.adjustXZ( location, 1, facing );
				location.setY( location.getBlockY() - 0.75 ); // because the teleporter sign shld be 1 block above the ground
				location.setX( location.getBlockX() + 0.5 );
				location.setZ( location.getBlockZ() + 0.5 );
			}

			if ( location != null ) {
				F.teleport( SafeSpawn.instance(), event.getPlayer(), location );
				Log.line( "Teleport to %s", F.toString( location ) );
				return true;
			}

			// ItemStack itemStack = event.getPlayer().getItemInHand();
			// if ( itemStack != null ) {
			// itemInHand = MagicWord.findItemByMaterial( itemStack.getType() );
			//
			// // line 1
			// String line1 = sign.getLine( 0 );
			// String l1Parts[] = line1.split( Const.MW_separator );
			// requiredInHand = MagicWord.findItemByWord( l1Parts[1] );
			//
			// if ( !requiredInHand.matches( itemInHand ) ) {
			// return false;
			// }
			//
			// String worldWord = l1Parts[2];
			// worldName = SafeSpawn.instance().getWorldsManager().findByMagic( worldWord );
			// if ( worldName == null )
			// return false;
			// World world = SafeSpawn.instance().getServer().getWorld( worldName );
			// if ( world == null )
			// return false;
			//
			// // line 2
			// String line2 = sign.getLine( 1 );
			// x = MagicWords.readCoordinateFromLine( line2 );
			//
			// String line3 = sign.getLine( 2 );
			// y = MagicWords.readCoordinateFromLine( line3 );
			//
			// String line4 = sign.getLine( 3 );
			// z = MagicWords.readCoordinateFromLine( line4 );
			//
			// if ( world != null ) {
			// Location location = new Location( world, x, y, z );
			// F.teleport( SafeSpawn.instance(), event.getPlayer(), location );
			// return true;
			// }
			// }
		} catch ( Exception e ) {
			Log.error( e ); // TODO Mute me
		}
		return false;
	}

	public static String toTeleporterRef( String bindingWord, String ownerName ) {
		return ownerName + "_" + bindingWord;
	}
}
