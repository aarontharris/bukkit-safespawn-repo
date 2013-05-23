package com.ath.bukkit.safespawn.magic.sign;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class MagicSign {

	/** return true if sign was activated */
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		return false;
	}

	public boolean enhance( Sign sign, Player player ) {
		return false;
	}
}
