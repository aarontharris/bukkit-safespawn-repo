package com.ath.bukkit.safespawn.data.magicsign;

import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShrineSign extends MagicSign {

	@Override
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		return super.activateSign( sign, event );
	}

}
