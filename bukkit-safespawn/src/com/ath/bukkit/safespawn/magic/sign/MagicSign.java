package com.ath.bukkit.safespawn.magic.sign;

import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;

public class MagicSign {

	/** return true if sign was activated */
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		return false;
	}

}
