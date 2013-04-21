package com.ath.bukkit.safespawn;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EggHatchery {

	public static enum EggStackType {
		Creeper( 50 ),
		Skeleton( 51 ),
		Spider( 52 ),
		Zombie( 54 ),
		Slime( 55 ),
		Ghast( 56 ),
		ZombiePigmen( 57 ),
		Endermen( 58 ),
		CaveSpider( 59 ),
		Silverfish( 60 ),
		Blaze( 61 ),
		MagmaCube( 62 ),
		Bat( 65 ),
		Witch( 66 ),
		Pig( 90 ),
		Sheep( 91 ),
		Cow( 92 ),
		Chicken( 93 ),
		Squid( 94 ),
		Wolf( 95 ),
		Mooshroom( 96 ),
		Ocelot( 98 ),
		Villager( 120 ), ;

		private short id;

		EggStackType( int id ) {
			this.id = (short) id;
		}
	}


	public static ItemStack newEgg( EggStackType type ) {
		try {
			ItemStack out = new ItemStack( 383, 1 );
			out.setDurability( type.id );
			return out;
		} catch ( Exception e ) {
			SafeSpawn.logError( e );
		}
		return null;
	}

}
