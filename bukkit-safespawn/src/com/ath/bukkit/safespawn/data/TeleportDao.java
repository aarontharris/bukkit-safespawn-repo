package com.ath.bukkit.safespawn.data;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

public class TeleportDao extends BaseDao {

	public TeleportDao( JavaPlugin plugin ) {
		super( plugin );
	}

	public void writeTeleportData( TeleporterData data ) throws Exception {
		write( data.toJson().toString(), "teleporters.json" );
	}

	public TeleporterData readTeleportData() throws Exception {
		JSONObject json = read( "teleporters.json" );
		TeleporterData out = new TeleporterData();
		out.fromJson( json );
		return out;
	}

}
