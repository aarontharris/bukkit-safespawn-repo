package com.ath.bukkit.safespawn.data;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

public class PlayerDao extends BaseDao {

	public PlayerDao( JavaPlugin plugin ) {
		super( plugin );
	}

	public void writePlayerData( PlayerData data, Player player ) throws Exception {
		write( data.toJson().toString(), player.getName() + ".json" );
	}

	/** never null */
	public PlayerData readPlayerData( Player player ) throws Exception {
		JSONObject json = read( player.getName() + ".json" );
		PlayerData out = new PlayerData();
		out.fromJson( json );
		return out;
	}
}
