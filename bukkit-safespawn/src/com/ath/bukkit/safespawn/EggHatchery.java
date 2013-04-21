package com.ath.bukkit.safespawn;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EggHatchery {

	// 383:50 Spawn Egg (Creeper)
	// 383:51 Spawn Egg (Skeleton)
	// 383:52 Spawn Egg (Spider)
	// 383:54 Spawn Egg (Zombie)
	// 383:55 Spawn Egg (Slime)
	// 383:56 Spawn Egg (Ghast)
	// 383:57 Spawn Egg (Zombie Pigmen)
	// 383:58 Spawn Egg (Endermen)
	// 383:59 Spawn Egg (Cave Spider)
	// 383:60 Spawn Egg (Silverfish)
	// 383:61 Spawn Egg (Blaze)
	// 383:62 Spawn Egg (Magma Cube)
	// 383:65 Spawn Egg (Bat)
	// 383:66 Spawn Egg (Witch)
	// 383:90 Spawn Egg (Pig)
	// 383:91 Spawn Egg (Sheep)
	// 383:92 Spawn Egg (Cow)
	// 383:93 Spawn Egg (Chicken)
	// 383:94 Spawn Egg (Squid)
	// 383:95 Spawn Egg (Wolf)
	// 383:96 Spawn Egg (Mooshroom)
	// 383:98 Spawn Egg (Ocelot)
	// 383:120 Spawn Egg (Villager)

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
		ItemStack out = new ItemStack( Material.EGG );
		out.setDurability( type.id );
		return null;
	}

}
