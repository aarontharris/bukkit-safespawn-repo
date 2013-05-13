package com.ath.bukkit.safespawn.ctrl.levitate;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ath.bukkit.safespawn.Log;
import com.ath.bukkit.safespawn.SafeSpawn;
import com.ath.bukkit.safespawn.data.PlayerData;

public class PlayerLevStateCtrl {

	public static void onPlayerJoinEvent( SafeSpawn plugin, PlayerJoinEvent event ) {
		try {
			PlayerData data = PlayerData.get( event.getPlayer() );
		} catch ( Exception e ) {
			Log.error( e );
		}
	}

	public static void onPlayerQuitEvent( SafeSpawn plugin, PlayerQuitEvent event ) {
	}

}
