package com.ath.bukkit.safespawn.data;

import org.json.simple.JSONObject;

public interface JSONSerializable {

	public JSONObject toJson() throws Exception;

	public void fromJson( JSONObject json ) throws Exception;

}
