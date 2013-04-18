package com.ath.bukkit.safespawn.data.magicsign;

import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ath.bukkit.safespawn.data.magicsign.SignReader.MagicSignType;

public class MagicSign {

	private MagicSignType type;

	public MagicSignType getType() {
		return type;
	}

	public void setType( MagicSignType type ) {
		this.type = type;
	}

	/** return true if sign was activated */
	public boolean activateSign( Sign sign, PlayerInteractEvent event ) {
		return false;
	}

}
